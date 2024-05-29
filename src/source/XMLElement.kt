package source

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

// Classe para representar um elemento XML
open class XMLElement(var name: String="") {
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

    fun getAttributeValue(name:String):String{
        var attributeValue = ""
        if (hasAttribute(name)){
            attributes.forEach {
                if (it.name == name){
                    attributeValue = it.value
                }
            }
        }else {
            attributeValue = ""
        }
        return attributeValue
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


    // UTILIZAR O ACCEPT PARA FAZER O VARRIMENTO E PESQUISA
    // Método para aceitar um visitante XML
    fun accept(visitor: XMLVisitor) {
        // Chamando o método visit do visitante para visitar este elemento
        visitor.visit(this)
        // Chamando o método accept de cada filho para permitir a visitação recursiva
        children.forEach { it.accept(visitor) }
    }

//    // Método para imprimir o elemento de forma bonita
//    fun prettyPrint(depth: Int = 0): String {
//        val indent = "\t".repeat(depth) // Gerando a string de indentação baseada na profundidade
//        val result = StringBuilder() // StringBuilder para construir a string de saída
//
//        // Adicionando a tag de abertura do elemento com seus atributos
//        result.append("$indent<$name")
//        //FALTA CENÁRIO PARA IR ATTRIBUTES.ISNOTEMPTY
//        if (attributes.isNotEmpty()) {
//            attributes.forEach { result.append(" ${it.name}=\"${it.value}\"") }
//            result.append(">")
//        } else {
//            result.append("/>")
//        }
//
//        // Adicionando uma quebra de linha
//        result.append("\n")
//
//        // Imprimindo os elementos filhos de forma recursiva
//        children.forEach { result.append(it.prettyPrint(depth + 1)) }
//
//        // Adicionando a tag de fechamento do elemento, se necessário
//        //ADICIONAR CASO DE TESTE PARA VER SE O CHILDREN IS NOT EMPTY
//        if (children.isNotEmpty()) {
//            result.append("$indent</$name>\n")
//        }
//
//        return result.toString() // Retornando a string de saída
//    }

    open fun prettyPrint(depth: Int = 0): String {
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

}