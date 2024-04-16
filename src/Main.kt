import javax.xml.xpath.XPath

// Classe que representa um atributo XML com um nome e um valor
data class XMLAttribute(var name: String, var value: String)

// Classe que representa um elemento XML
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

// Classe que representa um documento XML
class XMLDocument {
    // Elemento raiz do documento
    var rootElement: XMLElement? = null

    // Método para imprimir o documento de forma bonita
    fun prettyPrint(): String {
        // Verificando se há um elemento raiz
        if (rootElement == null) return "" // Retorna uma string vazia se não houver elemento raiz
        // Chamando o método prettyPrint do elemento raiz para iniciar a impressão
        return rootElement!!.prettyPrint()
    }

    // Método para escrever o documento em um arquivo
    fun writeToFile(fileName: String) {
        // Implementação para escrever o documento em um arquivo será adicionada posteriormente
        println("Documento escrito em $fileName")
    }

    // Método para aceitar um visitante XML
    fun accept(visitor: XMLVisitor) {
        rootElement?.accept(visitor)
    }

    // Método para adicionar atributos globalmente a todos os elementos filhos de um elemento específico
    fun addAttributesGlobal(nameEntity: String, nameAttributeEntity: String, valueAttributeEntity: String) {
        val listChildrenDescendants = rootElement?.findDescendants()
        listChildrenDescendants?.forEach {
            if (it.name == nameEntity)
                it.addAttribute(nameAttributeEntity, valueAttributeEntity)
        }
    }

    // Método auxiliar para obter o índice de um atributo
    private fun getIndexOfElement(nameAttribute: String, listAttributes: MutableList<XMLAttribute>): Int {
        listAttributes.forEachIndexed { index, attribute ->
            if (attribute.name == nameAttribute)
                return index
        }
        return -1
    }

    // Método para renomear atributos globalmente em todos os elementos filhos de um elemento específico
    fun renameAttributesGlobal(nameEntity: String, oldName: String, newName: String) {
        val listChildrenDescendants = rootElement?.findDescendants()
        listChildrenDescendants?.forEach { element ->
            if (element.name == nameEntity && element.hasAttribute(oldName)) {
                val indexAttribute = getIndexOfElement(oldName, element.attributes)
                if (indexAttribute != -1) {
                    element.attributes[indexAttribute].name = newName
                }
            }
        }
    }

    // Método para renomear entidades globalmente em todos os elementos filhos de um elemento específico
    fun renameEntitiesGlobal(oldNameEntity: String, newNameEntity: String) {
        val listOfChildrenDescendants = rootElement?.findDescendants()
        listOfChildrenDescendants?.forEach {
            if (it.name == oldNameEntity)
                it.name = newNameEntity
        }
    }
}

// Interface para visitantes XML
interface XMLVisitor {
    // Método para visitar um elemento XML
    fun visit(element: XMLElement)
}

class XPathEvaluator(private val document: XMLDocument) {
    // Método para avaliar uma expressão XPath e retornar uma lista de elementos correspondentes
    fun evaluate(expression: String):String/*List<XMLElement>*/ {
        val path = expression.split("/")
        var xPathEvaluatorString: String = ""
        var listXPathCompleted = mutableListOf<XMLElement>()
        var listTemp = mutableListOf(document.rootElement!!)


        fun aux(tagname:String, listXPath: MutableList<XMLElement>): MutableList<XMLElement> {
            var listTempAuxFun = listXPath.toMutableList()
            listXPath.forEach {
                if (it.name == tagname)
                    listTempAuxFun.addAll(it.children)
                listTempAuxFun.remove(it)
            }
            return listTempAuxFun
        }

        for (step in path){
           listXPathCompleted = aux(step, listTemp)
           listTemp = listXPathCompleted.toMutableList()
        }

       listXPathCompleted.forEach {
           xPathEvaluatorString = xPathEvaluatorString+it.prettyPrint()
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

    println(document.prettyPrint())
}