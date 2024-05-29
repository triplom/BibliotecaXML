package source

// Interface para visitantes XML
interface XMLVisitor {
    // Método para visitar um elemento XML
    fun visit(element: XMLElement)
}