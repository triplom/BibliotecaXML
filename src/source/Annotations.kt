package source

import kotlin.reflect.KClass

// Anotação para indicar uma classe que implementa a transformação a ser aplicada à string por padrão
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class XMLString(val value: KClass<out XMLStringAdapter<*>>) {
    class XMLStringAdapter<T> {

    }
}

// Anotação para indicar um atributo em XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttributeInXML()

// Anotação para associar um adaptador que realiza alterações na entidade XML após o mapeamento automático
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class XMLAdapter(val value: KClass<out XMLAdapterBase>)

// Anotação para personalizar a tradução para XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLProperty(
    val name: String, // Nome que a propriedade terá no XML
    val type: XMLType = XMLType.ATTRIBUTE, // Tipo de representação no XML (atributo, entidade ou objeto)
    val ignore: Boolean = false // Indica se a propriedade deve ser ignorada na geração do XML
)

// Anotação para ignorar o campo no XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLIgnore

// Anotação para marcar uma propriedade como um atributo XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLAttribute(val name: String = "", val value: String = "")

// Anotação para marcar uma propriedade como um elemento XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLElement(val isAttribute: Boolean = false, val s: String)

// Anotação para adicionar porcentagem
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLPercentage


annotation class XMLName(val value: String)