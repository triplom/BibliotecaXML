
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

// Anotação para indicar uma classe que implementa a transformação a ser aplicada à string por padrão
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlString(val value: KClass<out XmlStringAdapter<*>>)

// Anotação para associar um adaptador que realiza alterações na entidade XML após o mapeamento automático
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlAdapter(val value: KClass<out XmlAdapterBase>)

@Target(AnnotationTarget.PROPERTY)
annotation class RequiredElement

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class AttributeName(val attribute:String )

@Target(AnnotationTarget.CLASS)
annotation class DBname(val newName:String)

// Interface para adaptadores XML personalizados
interface XmlAdapterBase {
    fun adapt(element: XMLElement)
}

// Interface para adaptadores de string XML personalizados
interface XmlStringAdapter<T> {
    fun adapt(value: String): String
}

// Implementação padrão de um adaptador XML que não faz nada
class NoOpXmlAdapter : XmlAdapterBase {
    override fun adapt(element: XMLElement) {
        // Não faz nada
    }
}

// Implementação padrão de um adaptador de string XML que retorna o valor original
class NoOpXmlStringAdapter<T> : XmlStringAdapter<T> {
    override fun adapt(value: String): String {
        return value
    }
}

// Implementação de um adaptador de string XML que adiciona "%" ao valor
class AddPercentageXmlStringAdapter : XmlStringAdapter<Int> {
    override fun adapt(value: String): String {
        return "$value%"
    }
}

// Implementação do adaptador de personalização pós-mapeamento para a classe FUC
class FUCAdapter : XmlAdapterBase {
    override fun adapt(element: XMLElement) {
        // Implemente as alterações necessárias na entidade XML após o mapeamento automático para a classe FUC
    }
}

// Anotação para personalizar a tradução para XML
@Target(AnnotationTarget.PROPERTY)
annotation class XMLProperty(
    val name: String, // Nome que a propriedade terá no XML
    val type: XMLType = XMLType.ATTRIBUTE, // Tipo de representação no XML (atributo, entidade ou objeto)
    val ignore: Boolean = false // Indica se a propriedade deve ser ignorada na geração do XML
)

// Enumeração para os tipos de representação no XML
enum class XMLType {
    ATTRIBUTE, // Representação como atributo
    ENTITY,    // Representação como entidade (elemento filho)
    OBJECT     // Representação como objeto aninhado
}

// Função para converter um objeto em XML
fun Any.toXML(): XMLElement {
    val clazz = this::class

    // Obtendo o nome da classe para criar o elemento XML
    val xmlElement = XMLElement(clazz.simpleName ?: "unknown")

    // Itera sobre as propriedades da classe do objeto
    clazz.declaredMemberProperties.forEach { prop ->
        val value = prop.call(this)?.toString() ?: ""
        val attributeName = prop.name

        // Verificando se a propriedade está anotada com XMLProperty
        val annotation = prop.findAnnotation<XMLProperty>()

        if (annotation != null) {
            // Obtendo o nome personalizado para a propriedade no XML
            val propertyName = annotation.name

            // Verificando se a propriedade deve ser ignorada
            if (!annotation.ignore) {
                // Verificando o tipo de tradução (atributo, entidade ou objeto)
                when (annotation.type) {
                    XMLType.ATTRIBUTE -> {
                        // Se a propriedade estiver anotada como um atributo XML, adiciona-a como atributo
                        xmlElement.addAttribute(propertyName, value)
                    }
                    XMLType.ENTITY -> {
                        // Verificando se o valor da propriedade é um objeto
                        if (value.isNotEmpty()) {
                            // Convertendo recursivamente o objeto em XML e adicionando-o como entidade
                            val childElement = (prop.call(this) as? Any)?.toXML()
                            if (childElement != null)
                                xmlElement.addChild(childElement)
                        }
                    }
                    XMLType.OBJECT -> {
                        // Verificando se a propriedade está anotada com @XmlString para aplicar personalização
                        val stringAdapterAnnotation = prop.findAnnotation<XmlString>()
                        val adapter = stringAdapterAnnotation?.value?.java?.newInstance()
                                as? XmlStringAdapter<Any> ?: NoOpXmlStringAdapter()

                        // Aplicando a personalização ao valor da propriedade
                        val adaptedValue = adapter.adapt(value)

                        // Adicionando a propriedade como um atributo XML
                        xmlElement.addAttribute(propertyName, adaptedValue)
                    }
                }
            }
        } else {
            // Se não houver anotação, adiciona a propriedade como atributo com o mesmo nome
            xmlElement.addAttribute(attributeName, value)
        }
    }

    return xmlElement
}

// Classe para representar um atributo XML com um nome e um valor
data class XMLAttribute(var name: String, var value: String)

// Classe para representar um elemento XML
class XMLElement(var name: String) {
    // Lista de atributos do elemento
    val attributes = mutableListOf<XMLAttribute>()

    // Lista de elementos filhos
    val children = mutableListOf<XMLElement>()

    // Referência ao elemento pai
    private var parent: XMLElement? = null

    // Método para verificar se o elemento possui um determinado atributo
    fun hasAttribute(attribute: String): Boolean {
        return attributes.any { it.name == attribute }
    }

    // Método para adicionar um atributo ao elemento
    fun addAttribute(name: String, value: String) {
        attributes.add(XMLAttribute(name, value))
    }

    // Método para remover um atributo do elemento
    fun removeAttribute(name: String) {
        attributes.removeIf { it.name == name }
    }

    // Método para adicionar um elemento filho
    fun addChild(child: XMLElement) {
        child.parent = this
        children.add(child)
    }

    // Método para remover um elemento filho
    fun removeChild(child: XMLElement) {
        children.remove(child)
    }

    // Método para renomear o elemento
    fun rename(newName: String) {
        this.name = newName
    }


    // UTILIZAR O ACCEPT PARA FAZER O VARRIMENTO E PESQUISA
    // Método para aceitar um visitante XML
    fun accept(visitor: XMLVisitor) {
        // Chamando o método visit do visitante para visitar este elemento
        visitor.visit(this)
        // Chamando o método accept de cada filho para permitir a visitação recursiva
        children.forEach { it.accept(visitor) }
    }

    // Método para imprimir o elemento de forma bonita
    fun prettyPrint(depth: Int = 0): String {
        val indent = "\t".repeat(depth) // Gerando a string de indentação baseada na profundidade
        val result = StringBuilder() // StringBuilder para construir a string de saída

        // Adicionando a tag de abertura do elemento com seus atributos
        result.append("$indent<$name")
        //FALTA CENÁRIO PARA IR ATTRIBUTES.ISNOTEMPTY
        if (attributes.isNotEmpty()) {
            attributes.forEach { result.append(" ${it.name}=\"${it.value}\"") }
            result.append(">")
        } else {
            result.append("/>")
        }

        // Adicionando uma quebra de linha
        result.append("\n")

        // Imprimindo os elementos filhos de forma recursiva
        children.forEach { result.append(it.prettyPrint(depth + 1)) }

        // Adicionando a tag de fechamento do elemento, se necessário
        //ADICIONAR CASO DE TESTE PARA VER SE O CHILDREN IS NOT EMPTY
        if (children.isNotEmpty()) {
            result.append("$indent</$name>\n")
        }

        return result.toString() // Retornando a string de saída
    }

    // Método para encontrar a ascendência do elemento
    fun findAncestry(): List<XMLElement> {
        val listAncestry = mutableListOf<XMLElement>()
        var currentElement: XMLElement? = this.parent
        while (currentElement != null) {
            listAncestry.add(currentElement)
            currentElement = currentElement.parent
        }
        return listAncestry.reversed() // Revertendo a lista para ter a ordem correta
    }

    // Método para encontrar os descendentes do elemento
    fun findDescendants(): List<XMLElement> {
        val listDescendants = mutableListOf<XMLElement>()
        children.forEach {
            listDescendants.add(it)
            listDescendants.addAll(it.findDescendants())
        }
        return listDescendants
    }

}

// Classe representando um documento XML
class XMLDocument {

    //ADICIONAR CASOS DE TESTE PARA VALIDAR TODAS POSSIBILIDADES DE CENÁRIOS
    // Elemento raiz do documento
    var rootElement: XMLElement? = null

    // Método para adicionar atributos globalmente a todos os elementos filhos de um elemento específico
    fun addAttributesGlobal(nameEntity: String, nameAttributeEntity: String, valueAttributeEntity: String) {
        // Verifica se o documento e o elemento raiz não são nulos
        rootElement ?: throw IllegalStateException("RootElement is null")
        // Obtendo a árvore de elementos XML a partir do documento
        val xmlTree = rootElement?.findDescendants()

        // Iterando sobre a árvore de elementos XML
        xmlTree?.forEach { element ->
            // Verificando se o elemento possui o nome especificado
            if (element.name == nameEntity) {
                // Adicionando o atributo globalmente a todos os elementos filhos
                element.addAttribute(nameAttributeEntity, valueAttributeEntity)
            }
        }
    }

    // Método para renomear atributos globalmente em todos os elementos filhos de um elemento específico
    fun renameAttributesGlobal(nameEntity: String, oldName: String, newName: String) {
        // Verifica se o documento e o elemento raiz não são nulos
        rootElement ?: throw IllegalStateException("RootElement is null")
        // Obtendo a árvore de elementos XML a partir do documento
        val xmlTree = rootElement?.findDescendants()

        // Iterando sobre a árvore de elementos XML
        xmlTree?.forEach { element ->
            // Verificando se o elemento possui o nome especificado
            if (element.name == nameEntity) {
                // Renomeando o atributo em todos os elementos filhos
                element.attributes.find { it.name == oldName }?.name = newName
            }
        }
    }

    // Método para renomear entidades globalmente em todos os elementos filhos de um elemento específico
    fun renameEntitiesGlobal(oldNameEntity: String, newNameEntity: String) {
        // Verifica se o documento e o elemento raiz não são nulos
        rootElement ?: throw IllegalStateException("RootElement is null")
        // Obtendo a árvore de elementos XML a partir do documento
        val xmlTree = rootElement?.findDescendants()

        // Iterando sobre a árvore de elementos XML
        xmlTree?.forEach { element ->
            // Verificando se o elemento possui o nome especificado
            if (element.name == oldNameEntity) {
                // Renomeando a entidade em todos os elementos filhos
                element.rename(newNameEntity)
            }
        }
    }

    // Método auxiliar para obter o índice de um elemento
    private fun getIndexOfElement(nameAttribute: String, listAttributes: MutableList<XMLAttribute>): Int {
        // Obtendo o índice do elemento com o nome especificado
        return listAttributes.indexOfFirst { it.name == nameAttribute }
    }

    // Método para escrever o documento XML em um arquivo
    fun writeToFile(fileName: String) {
        // Verifica se o documento e o elemento raiz não são nulos
        rootElement ?: throw IllegalStateException("RootElement is null")
        val xmlString = rootElement?.prettyPrint() ?: ""
        File(fileName).writeText(xmlString)
    }

    // Método para aceitar um visitante XML
    fun accept(visitor: XMLVisitor) {
        rootElement?.accept(visitor)
    }
}

// Interface para visitantes XML
interface XMLVisitor {
    // Método para visitar um elemento XML
    fun visit(element: XMLElement)
}

class XPathEvaluator(private val document: XMLDocument) {
    // Método para avaliar uma expressão XPath e retornar uma lista de elementos correspondentes
    fun evaluate(expression: String): String {
        val path = expression.split("/")
        var xPathEvaluatorString = ""
        var listXPathCompleted = mutableListOf<XMLElement>()
        var listTemp = mutableListOf(document.rootElement!!)

        fun aux(tagname: String, listXPath: MutableList<XMLElement>): MutableList<XMLElement> {
            val listTempAuxFun = listXPath.toMutableList()
            listXPath.forEach {
                if (it.name == tagname) {
                    listTempAuxFun.addAll(it.children)
                    listTempAuxFun.remove(it)
                }
            }
            return listTempAuxFun
        }

        for (step in path) {
            listXPathCompleted = aux(step, listTemp)
            listTemp = listXPathCompleted.toMutableList()
        }

        listXPathCompleted.forEach {
            xPathEvaluatorString += it.prettyPrint()
        }

        return xPathEvaluatorString
    }
}
