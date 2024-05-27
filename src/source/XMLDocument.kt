package source

import java.io.File

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
}