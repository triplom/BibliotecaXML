package FUC_Exemplos

import source.XMLElement
import source.XMLName
import source.XMLPercentage
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

@XMLName("componente")
class ComponenteAvaliacao(
    @XMLName("Nome_elemento_avaliação")val nome: String,
    @XMLPercentage val peso: Int
) : XMLElement(){
    init {
        val objComponent = this::class
        objComponent.primaryConstructor!!.parameters.forEach { parameter ->
            val xmlNameAnnotation = if (parameter.hasAnnotation<XMLName>()) {
                parameter.findAnnotation<XMLName>()?.name.toString()
            } else {
                parameter.name.toString()
            }

            val property = this::class.members.find { it.name == parameter.name }
            val propertyValue = property?.call(this)

            if (parameter.hasAnnotation<XMLPercentage>()) {
                this.addAttribute(xmlNameAnnotation, "$propertyValue%")
            } else {
                this.addAttribute(xmlNameAnnotation, "$propertyValue")
            }
        }
    }

    override fun prettyPrint(depth: Int): String {
        val indent = "\t".repeat(depth) // Gerando a string de indentação baseada na profundidade
        val result = StringBuilder() // StringBuilder para construir a string de saída

        val objComponent = this::class
        val objName = if (objComponent.hasAnnotation<XMLName>()){
            objComponent.findAnnotation<XMLName>()?.name.toString()
        }else{
            objComponent.simpleName
        }

        // Adicionando a tag de abertura do elemento com seus atributos
        result.append("$indent<${objName}")
        attributes.forEach { result.append(" ${it.name}=\"${it.value}\"") }
        result.append("/>")


        return result.toString() // Retornando a string de saída
    }

}