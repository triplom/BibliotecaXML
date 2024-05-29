package source

// Anotação para indicar uma classe que implementa a transformação a ser aplicada à string por padrão

@Target(AnnotationTarget.PROPERTY)
annotation class XMLString()

// Anotação para indicar um atributo em XML
@Target(AnnotationTarget.PROPERTY)
annotation class AttributeInXML()

//// Anotação para associar um adaptador que realiza alterações na entidade XML após o mapeamento automático
//@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.CLASS)
//annotation class XMLAdapter(val value: KClass<out XMLAdapterBase>)

// Anotação para personalizar a tradução para XML
@Target(AnnotationTarget.PROPERTY)
annotation class XMLProperty(
    val name: String, // Nome que a propriedade terá no XML
    val type: XMLType = XMLType.ATTRIBUTE, // Tipo de representação no XML (atributo, entidade ou objeto)
    val ignore: Boolean = false // Indica se a propriedade deve ser ignorada na geração do XML
)

// Anotação para ignorar o campo no XML
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class XMLIgnore

// Anotação para marcar uma propriedade como um atributo XML
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class XMLAttribute_Annotation(val name: String = "", val value: String = "")

// Anotação para marcar uma propriedade como um elemento XML
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class XMLElement_Annotation(val isAttribute: Boolean = false)

// Anotação para adicionar porcentagem
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class XMLPercentage()

// Annotation to change the name
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class XMLName(val name:String = "")