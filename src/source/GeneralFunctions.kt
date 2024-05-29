package source
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation


// Interface para adaptadores XML personalizados
interface XmlAdapterBase {
    fun adapt(element: XMLElement)
}

// Interface para adaptadores de string XML personalizados
interface XmlStringAdapter<T> {
    fun adapt(value: String): String
}


// Implementação padrão de um adaptador de string XML que retorna o valor original
class NoOpXmlStringAdapter<T> : XmlStringAdapter<T> {
    override fun adapt(value: String): String {
        return value
    }
}

// Implementação de um adaptador de string XML que adiciona "%" ao valor
class AddPercentageXmlStringAdapter : XmlStringAdapter<Int> {
    override fun adapt(value: String): String {
        return "$value%"
    }
}

// Implementação do adaptador de personalização pós-mapeamento para a classe FUC
/*
class FUCAdapter : XmlAdapterBase {
    override fun adapt(element: XMLElement) {
        // Implemente as alterações necessárias na entidade XML após o mapeamento automático para a classe FUC
    }
}*/

// Função para converter um objeto em XML
fun Any.toXML(): XMLElement {
    val clazz = this::class

    // Obtendo o nome da classe para criar o elemento XML
    val xmlElement = XMLElement(clazz.simpleName ?: "unknown")

    // Itera sobre as propriedades da classe do objeto
    clazz.declaredMemberProperties.forEach { prop ->
        val value = prop.call(this)?.toString() ?: ""
        val attributeName = prop.name

        // Verificando se a propriedade está anotada com XMLProperty
        val annotation = prop.findAnnotation<XMLProperty>()

        if (annotation != null) {
            // Obtendo o nome personalizado para a propriedade no XML
            val propertyName = annotation.name

            // Verificando se a propriedade deve ser ignorada
            if (!annotation.ignore) {
                // Verificando o tipo de tradução (atributo, entidade ou objeto)
                when (annotation.type) {
                    XMLType.ATTRIBUTE -> {
                        // Se a propriedade estiver anotada como um atributo XML, adiciona-a como atributo
                        xmlElement.addAttribute(propertyName, value)
                    }
                    XMLType.ENTITY -> {
                        // Verificando se o valor da propriedade é um objeto
                        if (value.isNotEmpty()) {
                            // Convertendo recursivamente o objeto em XML e adicionando-o como entidade
                            val childElement = (prop.call(this) as? Any)?.toXML()
                            if (childElement != null)
                                xmlElement.addChild(childElement)
                        }
                    }
                    XMLType.OBJECT -> {
//                        // Verificando se a propriedade está anotada com @XmlString para aplicar personalização
//                        val stringAdapterAnnotation = prop.findAnnotation<XmlString>()
//                        val adapter = stringAdapterAnnotation?.value?.java?.newInstance()
//                                as? XmlStringAdapter<Any> ?: NoOpXmlStringAdapter()
//
//                        // Aplicando a personalização ao valor da propriedade
//                        val adaptedValue = adapter.adapt(value)
//
//                        // Adicionando a propriedade como um atributo XML
//                        xmlElement.addAttribute(propertyName, adaptedValue)
                    }
                }
            }
        } else {
            // Se não houver anotação, adiciona a propriedade como atributo com o mesmo nome
            xmlElement.addAttribute(attributeName, value)
        }
    }

    return xmlElement
}
