package source

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

// ===================================
// Annotations
// ===================================

// Anotação para indicar uma classe que implementa a transformação a ser aplicada à string por padrão
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class XMLString(val value: KClass<out XMLStringAdapter<*>>) {
    class XMLStringAdapter<T> {

    }
}

// Anotação para indicar um Documento em XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLDocument

// Anotação para indicar um atributo em XML
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttributeInXML()

// Anotação para associar um adaptador que realiza alterações na entidade XML após o mapeamento automático
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class XMLAdapter(val value: KClass<out XMLAdapterBase>)

class XMLAdapterBase {

}

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
annotation class XMLElement(val isAttribute: Boolean = false)

// Anotação para adicionar porcentagem
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLPercentage

data class Person(
    @XMLProperty(name = "firstName", type = XMLType.ATTRIBUTE)
    val firstName: String,

    @XMLProperty(name = "lastName", type = XMLType.ATTRIBUTE)
    val lastName: String,

    @XMLProperty(name = "age", type = XMLType.ATTRIBUTE)
    val age: Int,

    @XMLIgnore
    val password: String,

    @XMLProperty(name = "address", type = XMLType.ENTITY)
    val address: Address
)

data class Address(
    @XMLProperty(name = "street", type = XMLType.ATTRIBUTE)
    val street: String,

    @XMLProperty(name = "city", type = XMLType.ATTRIBUTE)
    val city: String
)





// ===================================
// Interfaces
// ===================================

interface XMLStringAdapter<T> {
    fun adapt(value: String): String
}

// ===================================
// Adapter Implementations
// ===================================

class NoOpXmlStringAdapter<T> : XMLStringAdapter<T> {
    override fun adapt(value: String): String {
        return value
    }
}

class AddPercentageXmlStringAdapter : XMLStringAdapter<Int> {
    override fun adapt(value: String): String {
        return "$value%"
    }
}

// ===================================
// XML Utilities
// ===================================


// Enumeração para os tipos de representação no XML
enum class XMLType {
    ATTRIBUTE, // Representação como atributo
    ENTITY,    // Representação como entidade (elemento filho)
    OBJECT     // Representação como objeto aninhado
}

// Classe para representar um atributo XML com um nome e um valor
class XMLAttr(
    @XMLAttribute
    var name: String, var value: String
)

class XElement(
    @XMLElement
    var name: String) {
    val attributes = mutableListOf<XMLAttr>()
    val children = mutableListOf<XElement>()
    private var parent: XElement? = null

    fun hasAttribute(attribute: String): Boolean {
        return attributes.any { it.name == attribute }
    }

    fun addAttribute(name: String, value: String) {
        attributes.add(XMLAttr(name, value))
    }

    fun removeAttribute(name: String) {
        attributes.removeIf { it.name == name }
    }

    fun addChild(child: XElement) {
        child.parent = this
        children.add(child)
    }

    fun removeChild(child: XElement) {
        children.remove(child)
    }

    fun rename(newName: String) {
        this.name = newName
    }

    fun accept(visitor: XMLVisitor) {
        visitor.visit(this)
        children.forEach { it.accept(visitor) }
    }

    fun prettyPrint(depth: Int = 0): String {
        val indent = "\t".repeat(depth)
        val result = StringBuilder()
        result.append("$indent<$name")
        attributes.forEach { result.append(" ${it.name}=\"${it.value}\"") }
        if (children.isNotEmpty()) {
            result.append(">\n")
            children.forEach { result.append(it.prettyPrint(depth + 1)) }
            result.append("$indent</$name>\n")
        } else {
            result.append("/>\n")
        }
        return result.toString()
    }

    fun findAncestry(): List<XElement> {
        val listAncestry = mutableListOf<XElement>()
        var currentElement: XElement? = this.parent
        while (currentElement != null) {
            listAncestry.add(currentElement)
            currentElement = currentElement.parent
        }
        return listAncestry.reversed()
    }

    fun findDescendants(): List<XElement> {
        val listDescendants = mutableListOf<XElement>()
        children.forEach {
            listDescendants.add(it)
            listDescendants.addAll(it.findDescendants())
        }
        return listDescendants
    }
}

interface XMLVisitor {
    fun visit(element: XElement)
}

class XMLDoc {
    var rootElement: XElement? = null

    fun addAttributesGlobal(nameEntity: String, nameAttributeEntity: String, valueAttributeEntity: String) {
        rootElement?.findDescendants()?.forEach { element ->
            if (element.name == nameEntity) {
                element.addAttribute(nameAttributeEntity, valueAttributeEntity)
            }
        }
    }

    fun renameAttributesGlobal(nameEntity: String, oldName: String, newName: String) {
        rootElement?.findDescendants()?.forEach { element ->
            if (element.name == nameEntity) {
                element.attributes.find { it.name == oldName }?.name = newName
            }
        }
    }

    fun renameEntitiesGlobal(oldNameEntity: String, newNameEntity: String) {
        rootElement?.findDescendants()?.forEach { element ->
            if (element.name == oldNameEntity) {
                element.rename(newNameEntity)
            }
        }
    }
}

class XPathEval(private val document: XMLDoc) {
    fun evaluate(expression: String): List<XElement> {
        val path = expression.split("/")
        var elements = mutableListOf(document.rootElement ?: return emptyList())

        for (step in path) {
            elements = elements.flatMap { it.children.filter { child -> child.name == step } }.toMutableList()
        }

        return elements
    }
}

fun Any.toXML(): XElement {
    val clazz = this::class
    val xmlElement = XElement(clazz.simpleName ?: "unknown")

    clazz.declaredMemberProperties.forEach { prop ->
        val value = prop.call(this)?.toString() ?: ""
        val attributeName = prop.name
        val annotation = prop.findAnnotation<XMLProperty>()

        if (annotation != null && !annotation.ignore) {
            val propertyName = annotation.name
            when (annotation.type) {
                XMLType.ATTRIBUTE -> xmlElement.addAttribute(propertyName, value)
                XMLType.ENTITY -> if (value.isNotEmpty()) xmlElement.addChild((prop.call(this) as? Any)?.toXML()!!)
                XMLType.OBJECT -> {
                    val stringAdapter = (prop.findAnnotation<XMLString>()?.value?.java?.newInstance() as? XMLStringAdapter<Any>) ?: NoOpXmlStringAdapter()
                    val adaptedValue = stringAdapter.adapt(value)
                    xmlElement.addAttribute(propertyName, adaptedValue)
                }
            }
        } else {
            xmlElement.addAttribute(attributeName, value)
        }
    }
    return xmlElement
}
