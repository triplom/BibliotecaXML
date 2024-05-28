import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import source.*
import kotlin.reflect.full.declaredMemberProperties

// Testes para a funcionalidade da classe XMLDocument
class XMLDocumentTest {

    private lateinit var document: source.XMLDocument

    // Configuração inicial para cada teste: criamos um documento com um elemento raiz "root" e um elemento filho "child".
    @BeforeEach
    fun setUp() {
        val root = XMLElement(isAttribute = true, "root")
        val child = XMLElement(isAttribute = true, "child")
        root.addChild(child)
        document = source.XMLDocument()
        document.rootElement = root
    }

    // Testes para manipulação de atributos
    @Test
    fun `test add attribute`() {
        val element = XMLElement("element", "root")
        element.addAttribute("attribute", "value")
        assertEquals(1, element.attributes.size)
    }

    @Test
    fun `test remove attribute`() {
        val element = XMLElement("element", "root")
        element.addAttribute("attribute", "value")
        element.removeAttribute("attribute")
        assertEquals(0, element.attributes.size)
    }

    // Testes para manipulação de elementos filhos
    @Test
    fun `test add entity`() {
        val parent = XMLElement("parent", "root")
        val child = XMLElement("child", "root")
        parent.addChild(child)
        assertEquals(1, parent.children.size)
    }

    @Test
    fun `test remove entity`() {
        val parent = XMLElement("parent", "root")
        val child = XMLElement("child", "root")
        parent.addChild(child)
        parent.removeChild(child)
        assertEquals(0, parent.children.size)
    }

    // Testes para navegação na hierarquia do XML
    @Test
    fun `test find ancestry`() {
        val root = XMLElement("root", "root")
        val child1 = XMLElement("child1", "root")
        val child2 = XMLElement("child2", "root")
        val child3 = XMLElement("child3", "root")
        val child4 = XMLElement("child4", "root")

        root.addChild(child1)
        root.addChild(child2)
        //child1.addChild(child2)
        child1.addChild(child3)
        child3.addChild(child4)

        val expectedAncestryNames = mutableListOf<XMLElement>(root, child1, child3)
       // val actualAncestryNames = child4.findAncestry().map { it.name }

        //assertIterableEquals(expectedAncestryNames, actualAncestryNames)
        assertEquals(/* expected = */ expectedAncestryNames,/* actual = */ child4.findAncestry())
    }

    @Test
    fun `test find descendants`() {
        val child4 = XMLElement("child4", "root")
        val child3 = XMLElement("child3", "root")
        val child2 = XMLElement("child2", "root")
        val child1 = XMLElement("child1", "root")
        val parent = XMLElement("parent", "root")

        parent.addChild(child1)
        child1.addChild(child2)
        child2.addChild(child3)
        child3.addChild(child4)

        val descendantsList = mutableListOf(child1, child2, child3, child4)
        assertEquals(descendantsList, parent.findDescendants())
    }

    @Test
    fun `test has attribute`() {
        val child = XMLElement("child", "root")
        child.addAttribute("Test", "Hello")
        assertTrue(child.hasAttribute("Test"))
        assertFalse(child.hasAttribute("Another"))
    }

    // Testes para manipulação global de atributos e elementos
    @Test
    fun `test add attribute globally`() {
        document.addAttributesGlobal("child", "AnotherTest", "World")
        assertEquals("World", document.rootElement?.children?.first()?.attributes?.find { it.name == "AnotherTest" }?.value)
    }

//    @Test
//    fun `test rename attribute globally`() {
//        val child = document.rootElement?.children?.first()
//        child?.addAttribute("OldName", "Value")
//        document.renameAttributesGlobal("child", "OldName", "NewName")
//        assertEquals("Value", child?.attributes?.find { it.name == "NewName" }?.value)
//        assertNull(child?.attributes?.find { it.name == "OldName" })
//    }

    @Test
    fun `test rename entity globally`() {
        val child = document.rootElement?.children?.first()
        document.renameEntitiesGlobal("child", "newChild")
        assertEquals("newChild", child?.name)
    }

    @Test
    fun testRenameEntityGlobal() {
        val root = XMLElement("root", "root")
        val child = XMLElement("child", "root")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root
        child.addAttribute("Teste", "Hello")
        val child2 = XMLElement("child", "root")
        child.addChild(child2)


        document.renameEntitiesGlobal("child", "Batata")

        assertEquals(true, child2.name == "Batata")
        assertEquals(true, child.name == "Batata")
        assertEquals(false, child.name == "child")
    }
}

// Testes para a funcionalidade da classe XMLElement
class XMLElementTest {

    // Teste para garantir que os métodos de manipulação de elementos funcionam corretamente
    @Test
    fun testXMLElement() {
        val element = XMLElement("person", "root")
        element.addAttribute("name", "John")
        element.addAttribute("age", "30")
        element.addChild(XMLElement("address", "root").apply {
            addAttribute("city", "New York")
            addAttribute("zip", "10001")
        })

        // Testando os métodos de manipulação de atributos
        assertEquals(2, element.attributes.size)
        assertEquals(1, element.children.size)

        element.removeAttribute("age")
        assertEquals(1, element.attributes.size)

        // Testando os métodos de manipulação de elementos filhos
        element.removeChild(element.children.first())
        assertEquals(0, element.children.size)

        // Testando o método de renomear
        element.rename("user")
        assertEquals("user", element.name)
    }

    @Test
    fun testPrettyPrint(): Unit {
        val root = XMLElement("root", "root")
        val child = XMLElement("child", "root")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root

        val expected = "<root>\n\t<child/>\n</root>"
        val actual = document.rootElement!!.prettyPrint().trim() // Remover espaços em branco extras

        assertEquals(expected, actual)


        val child2 = XMLElement("child2", "root")
        child.addChild(child2)
        val expectedwithDescendants = "<root>\n\t<child>\n\t\t<child2/>" +
                "\n\t</child>\n</root>"

        assertEquals(expectedwithDescendants, document.rootElement!!.prettyPrint().trim())
    }
}



// Testes para a funcionalidade de XPath e avaliação no documento XML
class XMLTests {

//    // Teste para avaliar a expressão XPath no documento XML
//    @Test
//    fun testXPathEvaluation() {
//        // Criando um documento com um elemento raiz "root" e cinco elementos filhos "child1" a "child5"
//        val document = source.XMLDocument()
//        val root = XMLElement("root")
//        val child = XMLElement("child")
//        document.rootElement = root
//        root.addChild(child)
//        for (i in 1..5) {
//            val secondLayerChild = XMLElement("child$i")
//            child.addChild(secondLayerChild)
//        }
//
//        // Avaliando a expressão XPath "root/child" no documento
//        val evaluator = XPathEvaluator(document)
//        val result = evaluator.evaluate("root/child")
//
//
//        assertEquals("<child1/>\n<child2/>\n<child3/>\n<child4/>\n<child5/>\n", result)
//
//        val secondChild = child.children.first()
//        secondChild.addAttribute("age","123")
//        val resultwithAttributes = evaluator.evaluate("root/child")
//        secondChild.addChild(XMLElement("neto"))
//        assertEquals("<child1 age=\"123\">\n<neto/></child1><child2/>\n<child3/>\n<child4/>\n<child5/>\n", resultwithAttributes)
//
//    }

    // Teste para verificar a avaliação de XPath em um documento XML
    @Test
    fun testXPathEvaluation2() {
        val root = XMLElement("root", "root")
        val child1 = XMLElement("child", "root")
        val child2 = XMLElement("child", "root")
        root.addChild(child1)
        root.addChild(child2)

        val document = XMLDocument()
        document.rootElement = root

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root")

        assertEquals(2, result.size)
    }
    // Teste para verificar a avaliação de XPath em um documento XML com um único elemento filho
    @Test
    fun testXPathEvaluationSingleChild() {
        val root = XMLElement("root", "root")
        val child = XMLElement("child", "root")
        val child1 = XMLElement("child1", "root")

        root.addChild(child)
        child.addChild(child1)


        val document = XMLDocument()
        document.rootElement = root

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        assertEquals(1, result.size)
    }

    // Teste para verificar a avaliação de XPath em um documento XML com atributos
    @Test
    fun testXPathEvaluationWithAttributes()  {
        val root = XMLElement("root", "root")
        val child = XMLElement("child", "root")
        val child1 = XMLElement("child1", "root")
        child1.addAttribute("attribute1", "value1")
        child1.addAttribute("attribute2", "value2")
        root.addChild(child)
        child.addChild(child1)

        val document = XMLDocument()
        document.rootElement = root

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        assertEquals(1, result.size)
        assertEquals(2, result[0].attributes.size)
        assertEquals("value1", result[0].attributes[0].value)
        assertEquals("attribute1", result[0].attributes[0].name)
        assertEquals("value2", result[0].attributes[1].value)
        assertEquals("attribute2", result[0].attributes[1].name)
    }

    // Teste para verificar a avaliação de XPath com vários níveis de filhos
    @Test
    fun testXPathEvaluationMultipleLevels() {
        val parent = XMLElement("parent", "root")
        val child1 = XMLElement("child1", "root")
        val child2 = XMLElement("child2", "root")
        child1.addChild(child2)
        parent.addChild(child1)

        val document = XMLDocument()
        document.rootElement = parent

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("parent/child1")

        assertEquals(1, result.size)
    }
}

// Testes para a funcionalidade de reflexão XML
class XMLReflectionTest {

    // Teste para verificar se a reflexão funciona corretamente para uma classe de teste
    @Test
    fun testXMLReflection() {
        // Criando uma classe de teste para fins de demonstração
        data class TestClass(val field1: String, val field2: Int, val field3: Double)

        // Obtendo as propriedades da classe usando reflexão
        val properties = TestClass::class.declaredMemberProperties

        // Verificando se o número de propriedades é correto e se seus nomes estão corretos
        assertEquals(3, properties.size)
        assertEquals("field1", properties.elementAt(0).name)
        assertEquals("field2", properties.elementAt(1).name)
        assertEquals("field3", properties.elementAt(2).name)
    }
}

// Testes added to Any.ToXML


class ToXMLTest {

    @Test
    fun testPersonToXML() {
        val address = Address("123 Main St", "Springfield")
        val person = Person("John", "Doe", 30, "secret", address)

        val xmlElement = person.toXML()

        assertEquals("Person", XMLElement.name)
        assertTrue(xmlElement.hasAttribute("firstName"))
        assertTrue(xmlElement.hasAttribute("lastName"))
        assertTrue(xmlElement.hasAttribute("age"))
        assertFalse(xmlElement.hasAttribute("password"))

        val firstNameAttr = xmlElement.attributes.find { it.name == "firstName" }
        assertNotNull(firstNameAttr)
        assertEquals("John", firstNameAttr?.value)

        val lastNameAttr = xmlElement.attributes.find { it.name == "lastName" }
        assertNotNull(lastNameAttr)
        assertEquals("Doe", lastNameAttr?.value)

        val ageAttr = xmlElement.attributes.find { it.name == "age" }
        assertNotNull(ageAttr)
        assertEquals("30", ageAttr?.value)

        assertEquals(1, xmlElement.children.size)

        val addressElement = xmlElement.children.find { it.name == "Address" }
        assertNotNull(addressElement)
        assertEquals("123 Main St", addressElement?.attributes?.find { it.name == "street" }?.value)
        assertEquals("Springfield", addressElement?.attributes?.find { it.name == "city" }?.value)
    }

    @Test
    fun testAddressToXML() {
        val address = Address("123 Main St", "Springfield")

        val xmlElement = address.toXML()

        assertEquals("Address", xmlElement.name)
        assertTrue(xmlElement.hasAttribute("street"))
        assertTrue(xmlElement.hasAttribute("city"))

        val streetAttr = xmlElement.attributes.find { it.name == "street" }
        assertNotNull(streetAttr)
        assertEquals("123 Main St", streetAttr?.value)

        val cityAttr = xmlElement.attributes.find { it.name == "city" }
        assertNotNull(cityAttr)
        assertEquals("Springfield", cityAttr?.value)
    }
}



// Testes para verificar a funcionalidade das anotações XML
class XMLAnnotationsTest {

    // Teste para verificar se as propriedades foram convertidas corretamente com as anotações
    @Test
    fun `test annotation conversion`() {
        // Criando uma classe com anotações XML para teste
        @XMLName("Person")
        data class Person(
            @XMLAttribute("name")
            val name: String,
            @XMLAttribute("age")
            val age: Int,
            @XMLElement("address", "root")
            val address: String
        )

        // Criando uma instância da classe
        val person = Person("John", 30, "123 Main St, Springfield")

        // Convertendo para XML
        val xmlElement = person.toXML()

        // Verificando se as anotações foram aplicadas corretamente
        assertEquals("Person", xmlElement.name)
        assertTrue(xmlElement.hasAttribute("name"))
        assertEquals("John", xmlElement.getAttributeValue("name"))
        assertTrue(xmlElement.hasAttribute("age"))
        assertEquals("30", xmlElement.getAttributeValue("age"))
        assertEquals(1, xmlElement.children.size)
        assertEquals("address", xmlElement.children[0].name)
        assertEquals("123 Main St, Springfield", xmlElement.children[0].text)
    }
}
