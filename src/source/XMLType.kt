package source

// Enumeração para os tipos de representação no XML
enum class XMLType {
    ATTRIBUTE, // Representação como atributo
    ENTITY,    // Representação como entidade (elemento filho)
    OBJECT     // Representação como objeto aninhado
}