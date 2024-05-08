package source


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