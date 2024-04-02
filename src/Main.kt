// Classe que representa um atributo XML com um nome e um valor
data class XMLAttribute(val name: String, val value: String)

// Classe que representa um elemento XML
class XMLElement(var name: String) {
    // Lista de atributos do elemento
    val attributes = mutableListOf<XMLAttribute>()

    // Lista de elementos filhos
    val children = mutableListOf<XMLElement>()

    // Referência ao elemento pai
    var parent: XMLElement? = null

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
        result.append(">\n")

        // Imprimindo os elementos filhos de forma recursiva
        children.forEach { result.append(it.prettyPrint(depth + 1)) }

        // Adicionando a tag de fechamento do elemento
        result.append("$indent</$name>\n")

        return result.toString() // Retornando a string de saída
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
    fun writeToFile() {
        // Implementação será adicionada posteriormente
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

// Classe que avalia expressões XPath em um documento XML
class XPathEvaluator(private val document: XMLDocument) {
    // Método para avaliar uma expressão XPath e retornar uma lista de elementos correspondentes
    fun evaluate(expression: String): List<XMLElement> {
        val path = expression.split("/")
        var elements = listOf(document.rootElement ?: return emptyList())
        for (step in path) {
            elements = elements.flatMap { it.children.filter { child -> child.name == step } }
        }
        return elements
    }
}