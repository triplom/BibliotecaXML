package FUC_Exemplos

import source.*
import kotlin.reflect.full.*

class FUC(
    val codigo: String,
    val nome: String,
    val ects: Double,
    val observacoes: String,
    val avaliacao: List<ComponenteAvaliacao>
) : XMLElement("fuc", "root"){

    val objectFUC = this::class

    init {
        objectFUC.primaryConstructor!!.parameters.forEach { parameter ->
            val xmlNameAnnotation = parameter.name
            val property = this::class.members.find { it.name == parameter.name }
            val propertyValue = property?.call(this)

            if (parameter.hasAnnotation<XMLIgnore>())

            if (parameter.hasAnnotation<XMLPercentage>()) {
                if (xmlNameAnnotation != null) {
                    this.addAttribute(xmlNameAnnotation, "$propertyValue%")
                }
            } else {
                if (xmlNameAnnotation != null) {
                    this.addAttribute(xmlNameAnnotation, "$propertyValue")
                }
            }
        }
    }

    override fun prettyPrint(depth: Int): String {



        val indent = "\t".repeat(depth) // Gerando a string de indentação baseada na profundidade
        val result = StringBuilder() // StringBuilder para construir a string de saída

        // Adicionando a tag de abertura do elemento com seus atributos
        result.append("$indent<$name")

        objectFUC.primaryConstructor!!.parameters.forEach {
            if (it.hasAnnotation<XMLAttribute>()){

            }
        }
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
    /*
        xml attribute para os atributos que devem estar juntos à primeira tag
        xml element para os atributos que são elementos tentar criar um dicionário para ter nome da tag e o seu valor
        xml ignore para ignorar os attributos
     */

}