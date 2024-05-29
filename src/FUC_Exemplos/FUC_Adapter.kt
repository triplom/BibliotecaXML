package FUC_Exemplos

import source.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

@XMLName("fuc")
class FUC(
    @XMLAttribute_Annotation val codigo: String,
    @XMLElement_Annotation val nome: String,
    @XMLElement_Annotation val ects: Double,
    @XMLIgnore val observacoes: String,
    @XMLElement_Annotation val avaliacao: List<ComponenteAvaliacao>
) : XMLElement("fuc"){

    val objectFUC = this::class

    val nameObject = objectFUC.findAnnotation<XMLName>()?.name.toString()
    override fun prettyPrint(depth: Int): String {

        val indent = "\t".repeat(depth) // Gerando a string de indentação baseada na profundidade
        val result = StringBuilder() // StringBuilder para construir a string de saída

        // Adicionando a tag de abertura do elemento com seus atributos
        result.append("$indent<$nameObject")

        objectFUC.primaryConstructor!!.parameters.forEach { parameter->
            if (parameter.hasAnnotation<XMLAttribute_Annotation>()){
                val xmlNameAnnotation = parameter.name
                val property = this::class.members.find { it.name == parameter.name }
                val propertyValue = property?.call(this)

                result.append(" ${xmlNameAnnotation}=\"${propertyValue}\"")
            }
        }
        result.append(">\n")
        objectFUC.primaryConstructor!!.parameters.forEach { parameter->
            if (parameter.hasAnnotation<XMLElement_Annotation>()){
                val xmlNameAnnotation = parameter.name
                val property = this::class.members.find { it.name == parameter.name }
                val propertyValue = property?.call(this)

                if (xmlNameAnnotation == "avaliacao"){
                    result.append("${indent}${indent}\t<${xmlNameAnnotation}>\n")
                    for (item in this.avaliacao){
                        result.append(item.prettyPrint(2))
                        result.append("\n")
                    }
                    result.append("${indent}${indent}\t</${xmlNameAnnotation}>\n")
                }else {
                    result.append("${indent}${indent}\t<${xmlNameAnnotation}>\"${propertyValue}\"</${xmlNameAnnotation}>\n")
                }
            }
        }
        result.append("$indent</$nameObject>")
        return result.toString() // Retornando a string de saída
    }

}