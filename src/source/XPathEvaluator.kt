package source


class XPathEvaluator(private val document: XMLDocument) {
    // Método para avaliar uma expressão XPath e retornar uma lista de elementos correspondentes

    fun evaluate(expression: String): List<XMLElement> {
        val path = expression.split("/")
        var elements: MutableList<XMLElement> = mutableListOf(document.rootElement ?: return emptyList())
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
            val filteredElements = aux(step,elements)
            elements = filteredElements.toMutableList()
        }
        return elements
    }
}