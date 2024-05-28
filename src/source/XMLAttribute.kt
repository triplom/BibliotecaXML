package source

// Classe para representar um atributo XML com um nome e um valor
data class XMLAttr(
    @XMLAttribute
    var name: String, var value: String
)

