package source

import kotlin.reflect.KClass

// Anotação para indicar uma classe que implementa a transformação a ser aplicada à string por padrão
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlString(val value: KClass<out XmlStringAdapter<*>>)

// Anotação para associar um adaptador que realiza alterações na entidade XML após o mapeamento automático
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlAdapter(val value: KClass<out XmlAdapterBase>)

// Anotação para personalizar a tradução para XML
@Target(AnnotationTarget.PROPERTY)
annotation class XMLProperty(
    val name: String, // Nome que a propriedade terá no XML
    val type: XMLType = XMLType.ATTRIBUTE, // Tipo de representação no XML (atributo, entidade ou objeto)
    val ignore: Boolean = false // Indica se a propriedade deve ser ignorada na geração do XML
)