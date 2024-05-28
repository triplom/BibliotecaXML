package FUC_Exemplos

import source.*
import kotlin.reflect.full.*

@XMLName("componente")
class ComponenteAvaliacao(
    val nome: String,
    @XMLPercentage val peso: Int, name: String
) : XMLElement(s = "root") {
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

}