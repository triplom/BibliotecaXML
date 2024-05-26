package FUC_Exemplos

import source.*
import kotlin.reflect.full.*

@XMLName("componente")
class ComponenteAvaliacao(
    @XMLName("grande nome")val nome: String,
    @XMLName("percentagem")@XMLPercentage val peso: Int){
    init {
        val objComponent = this::class
        val XMLElementToComponent = XMLElement("componente")
        objComponent.primaryConstructor!!.parameters.forEach {
            val attributeNameXML =
                if (it.hasAnnotation<XMLName>())
                    it.findAnnotation<XMLName>().toString()
                else it.name.toString()
            if (it.hasAnnotation<XMLPercentage>()){
               XMLElementToComponent.addAttribute(attributeNameXML , "%")
            }

        }
    }

}

fun main(){
    val test = ComponenteAvaliacao("Teste",12)
}