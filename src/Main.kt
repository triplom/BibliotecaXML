
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

// Anotação para personalizar a tradução para XML
@Target(AnnotationTarget.PROPERTY)
annotation class XMLProperty(val name: String, val type: XMLType = XMLType.ATTRIBUTE, val ignore: Boolean = false)

enum class XMLType {
    ATTRIBUTE, ENTITY
}

// Função para converter um objeto em XML
fun objectToXML(obj: Any): XMLElement {
    val clazz = obj::class

    // Obtendo o nome da classe para criar o elemento XML
    val xmlElement = XMLElement(clazz.simpleName ?: "unknown")

    // Iterando sobre as propriedades da classe
    clazz.declaredMemberProperties.forEach { prop ->
        // Verificando se a propriedade está anotada com XMLProperty
        val annotation = prop.findAnnotation<XMLProperty>()

        if (annotation != null) {
            // Obtendo o nome personalizado para a propriedade no XML
            val propertyName = annotation.name

            // Verificando se a propriedade deve ser ignorada
            if (!annotation.ignore) {
                val value = prop.get(obj as Nothing)

                // Verificando o tipo de tradução (atributo ou entidade)
                when (annotation.type) {
                    XMLType.ATTRIBUTE -> {
                        // Adicionando a propriedade como um atributo do elemento XML
                        xmlElement.addAttribute(propertyName, value.toString())
                    }
                    XMLType.ENTITY -> {
                        // Verificando se o valor da propriedade é um objeto
                        if (value is Any) {
                            // Convertendo recursivamente o objeto em XML
                            val childElement = objectToXML(value)
                            // Adicionando o elemento filho ao elemento XML
                            xmlElement.addChild(childElement)
                        }
                    }
                }
            }
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

    // Sobrescrevendo o método equals para comparar elementos XML
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XMLElement

        if (name != other.name) return false
        if (attributes != other.attributes) return false
        if (children != other.children) return false

        return true
    }

    // Sobrescrevendo o método hashCode para garantir consistência com equals
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }

}

// Classe representando um documento XML
class XMLDocument {
    // Elemento raiz do documento
    var rootElement: XMLElement? = null

    // Método para adicionar atributos globalmente a todos os elementos filhos de um elemento específico
    fun addAttributesGlobal(nameEntity: String, nameAttributeEntity: String, valueAttributeEntity: String) {
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
    private fun getIndexOfElement(nameAttribute: String, listAttributes: MutableList<XMLAttribute>) : Int {
        // Obtendo o índice do elemento com o nome especificado
        return listAttributes.indexOfFirst { it.name == nameAttribute }
    }

    // Método para escrever o documento XML em um arquivo
    fun writeToFile(fileName: String) {
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

fun main() {
    val document = XMLDocument()
    val root = XMLElement("root")
    document.rootElement = root

    // Adicionando vários filhos usando um loop
    for (i in 1..5) {
        val child = XMLElement("child$i")
        root.addChild(child)
    }

    // Renomeando o elemento raiz
    document.rootElement?.rename("newRoot")

    // Escrevendo o documento em um arquivo
    document.writeToFile("output.xml")

    // Adicionando vários atributos usando um loop
    val attributesToAdd = listOf(
        "attr1" to "value1",
        "attr2" to "value2",
        "attr3" to "value3"
    )
    for ((name, value) in attributesToAdd) {
        root.addAttribute(name, value)
    }

    document.accept(object : XMLVisitor {
        override fun visit(element: XMLElement) {
            println(element.name)
        }
    })

    println(document.rootElement?.prettyPrint())
}