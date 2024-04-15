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

    fun hasAttribute(attribute:String): Boolean{
        attributes.forEach {
            if (it.name == attribute)
                return true
        }
        return false
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
        attributes.forEach { result.append(" ${it.name}=\"${it.value}\"") }
        if (children.size > 0){
            result.append(">\n")
            // Imprimindo os elementos filhos de forma recursiva
            children.forEach { result.append(it.prettyPrint(depth + 1)) }
            // Adicionando a tag de fechamento do elemento
            result.append("$indent</$name>\n")
        }else{
            result.append("/>\n")
        }

        return result.toString() // Retornando a string de saída
    }

    fun findAncestry():MutableList<XMLElement>{
        val listAncestry = mutableListOf<XMLElement>()
        if (this.parent != null){
            listAncestry.add(this.parent!!)
            listAncestry.addAll(this.parent!!.findAncestry())
        }
        return listAncestry
    }

    fun findDescendants(): MutableList<XMLElement>{
        val listDescendants = mutableListOf<XMLElement>()
        this.children.forEach {
            listDescendants.add(it)
            listDescendants.addAll(it.findDescendants())
        }

        return listDescendants
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

    fun addAttributesGlobal(nameEntity: String, nameAttributeEntity:String, valueAttributeEntity: String){
        val listChildrenDescendants = rootElement?.findDescendants()
        listChildrenDescendants?.forEach {
            if (it.name == nameEntity)
                it.addAttribute(nameAttributeEntity,valueAttributeEntity)
        }
    }

    private fun getIndexOfElement(nameAttribute: String, listAttributes:MutableList<XMLAttribute>): Int{
        listAttributes.forEach {
            if (it.name == nameAttribute)
                 return listAttributes.indexOf(it)
        }
        return 0
    }
    fun renameAttributesGlobal(nameEntity:String, oldName:String, newName:String){
        val listChildrenDescendants = rootElement?.findDescendants()
        listChildrenDescendants?.forEach {
            if (it.name == nameEntity && it.hasAttribute(oldName)){
                val indexAttribute = getIndexOfElement(oldName, it.attributes)
                val attributeToChange = it.attributes.elementAt(indexAttribute)
                attributeToChange.name = newName
            }
        }
    }

    fun renameEntitiesGlobal(oldNameEntity: String, newNameEntity:String){
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

// Classe que avalia expressões XPath em um documento XML
class XPathEvaluator(private val document: XMLDocument) {
    // Método para avaliar uma expressão XPath e retornar uma lista de elementos correspondentes
    fun evaluate(expression: String): List<XMLElement> {
        val path = expression.split("/")
        var elements: List<XMLElement> = listOf(document.rootElement ?: return emptyList())
        for (step in path) {
            val filteredElements = mutableListOf<XMLElement>()
            for (element in elements) {
                filteredElements.addAll(element.children.filter { it.name == step })
            }
            elements = filteredElements
        }
        return elements
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
