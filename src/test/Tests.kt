
import FUC_Exemplos.ComponenteAvaliacao
import FUC_Exemplos.FUC
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import source.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import source.XMLDocument
import source.XMLElement
import source.XPathEvaluator
import kotlin.reflect.full.declaredMemberProperties

// Testes para a funcionalidade da classe XMLDocument
class XMLDocumentTest {

    private lateinit var document: source.XMLDocument

    // Configuração inicial para cada teste: criamos um documento com um elemento raiz "root" e um elemento filho "child".
    @BeforeEach
    fun setUp() {
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)
        document = source.XMLDocument()
        document.rootElement = root
    }

    // Testes para manipulação de atributos
    @Test
    fun `test add attribute`() {
        val element = XMLElement("element")
        element.addAttribute("attribute", "value")
        assertEquals(1, element.attributes.size)
    }

    @Test
    fun `test remove attribute`() {
        val element = XMLElement("element")
        element.addAttribute("attribute", "value")
        element.removeAttribute("attribute")
        assertEquals(0, element.attributes.size)
    }

    // Testes para manipulação de elementos filhos
    @Test
    fun `test add entity`() {
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addChild(child)
        assertEquals(1, parent.children.size)
    }

    @Test
    fun `test remove entity`() {
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addChild(child)
        parent.removeChild(child)
        assertEquals(0, parent.children.size)
    }

    // Testes para navegação na hierarquia do XML
    @Test
    fun `test find ancestry`() {
        val root = XMLElement("root")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
        val child3 = XMLElement("child3")
        val child4 = XMLElement("child4")

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
        val child4 = XMLElement("child4")
        val child3 = XMLElement("child3")
        val child2 = XMLElement("child2")
        val child1 = XMLElement("child1")
        val parent = XMLElement("parent")

        parent.addChild(child1)
        child1.addChild(child2)
        child2.addChild(child3)
        child3.addChild(child4)

        val descendantsList = mutableListOf(child1, child2, child3, child4)
        assertEquals(descendantsList, parent.findDescendants())
    }

    @Test
    fun `test has attribute`() {
        val child = XMLElement("child")
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

    @Test
    fun `test rename entity globally`() {
        val child = document.rootElement?.children?.first()
        document.renameEntitiesGlobal("child", "newChild")
        assertEquals("newChild", child?.name)
    }

    @Test
    fun testRenameEntityGlobal() {
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root
        child.addAttribute("Teste", "Hello")
        val child2 = XMLElement("child")
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
        val element = XMLElement("person")
        element.addAttribute("name", "John")
        element.addAttribute("age", "30")
        element.addChild(XMLElement("address").apply {
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
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root

        val expected = "<root>\n\t<child/>\n</root>"
        val actual = document.rootElement!!.prettyPrint().trim() // Remover espaços em branco extras

        assertEquals(expected, actual)


        val child2 = XMLElement("child2")
        child.addChild(child2)
        val expectedwithDescendants = "<root>\n\t<child>\n\t\t<child2/>" +
                "\n\t</child>\n</root>"

        assertEquals(expectedwithDescendants, document.rootElement!!.prettyPrint().trim())
    }
}



// Testes para a funcionalidade de XPath e avaliação no documento XML
class XMLTests {

    // Teste para verificar a avaliação de XPath em um documento XML
    @Test
    fun testXPathEvaluation2() {
        val root = XMLElement("root")
        val child1 = XMLElement("child")
        val child2 = XMLElement("child")
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
        val root = XMLElement("root")
        val child = XMLElement("child")
        val child1 = XMLElement("child1")

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
        val root = XMLElement("root")
        val child = XMLElement("child")
        val child1 = XMLElement("child1")
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
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
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

class testComponentAvaliacao{

    @Test
    fun test_pretty_princt_component_avaliacao(){
        val componentAvaliacaoExemplo = ComponenteAvaliacao("Trabalho Prático", 50)
        val resultComponentAvaliacao = componentAvaliacaoExemplo.prettyPrint()

        assertEquals("<componente Nome_elemento_avaliação=\"Trabalho Prático\" peso=\"50%\"/>", resultComponentAvaliacao)
    }
}

class testFUC{

    @Test
    fun test_pretty_print_FUC(){
        val componentAvaliacao1 = ComponenteAvaliacao("Quizes", 20)
        val componentAvaliacao2 = ComponenteAvaliacao("Trabalho Entrega Intercalar", 30)
        val componentAvaliacao3 = ComponenteAvaliacao("Trabalho Entrega Final", 50)

        val fucTest = FUC("M123456", "Programação Avançada", 6.0, "Desenvolver APIs", mutableListOf(componentAvaliacao1, componentAvaliacao2, componentAvaliacao3))
        val resultFUCTest = fucTest.prettyPrint()

        assertEquals("<fuc codigo=\"M123456\">\n" +
                "\t<nome>\"Programação Avançada\"</nome>\n" +
                "\t<ects>\"6.0\"</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente Nome_elemento_avaliação=\"Quizes\" peso=\"20%\"/>\n" +
                "\t\t<componente Nome_elemento_avaliação=\"Trabalho Entrega Intercalar\" peso=\"30%\"/>\n" +
                "\t\t<componente Nome_elemento_avaliação=\"Trabalho Entrega Final\" peso=\"50%\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>",resultFUCTest)
    }
}

// ## 27/05 ##
// Testes added to Any.ToXML

// Testes added to Any.ToXML
// Classes for testing
@XMLName("Person")
data class Person(
    @XMLAttribute_Annotation("firstName") val firstName: String,
    @XMLAttribute_Annotation("lastName") val lastName: String,
    @XMLAttribute_Annotation("age") val age: Int,
    val password: String, // This should not be included in XML
    val address: Address
)

@XMLName("Address")
data class Address(
    @XMLAttribute_Annotation("street") val street: String,
    @XMLAttribute_Annotation("city") val city: String
)

class ToXMLTest {

  @Test
    fun testPersonToXML() {
        val address = Address("123 Main St", "Springfield")
        val person =  Person("John", "Doe", 30, "secret", address)

        val xmlElement = person.toXML()

        assertEquals("Person", xmlElement.name)
        assertTrue(xmlElement.hasAttribute("firstName"))
        assertTrue(xmlElement.hasAttribute("lastName"))
        assertTrue(xmlElement.hasAttribute("age"))
        assertTrue(xmlElement.hasAttribute("password"))

        val firstNameAttr = xmlElement.attributes.find { it.name == "firstName" }
        assertNotNull(firstNameAttr)
        assertEquals("John", firstNameAttr?.value)

        val lastNameAttr = xmlElement.attributes.find { it.name == "lastName" }
        assertNotNull(lastNameAttr)
        assertEquals("Doe", lastNameAttr?.value)

        val ageAttr = xmlElement.attributes.find { it.name == "age" }
        assertNotNull(ageAttr)
        assertEquals("30", ageAttr?.value)

        assertEquals(0, xmlElement.children.size)

    }

    @Nested
    inner class XMLAnnotationsTest2 {

        // Teste para verificar se as propriedades foram convertidas corretamente com as anotações
        @Test
        fun `test annotation conversion`() {
            // Criando uma classe com anotações XML para teste
            @XMLName("Person")
            data class Person(
                @XMLAttribute_Annotation("name")
                val name: String,
                @XMLAttribute_Annotation("age")
                val age: Int,
                @XMLElement_Annotation(isAttribute = true)
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
            assertEquals(0, xmlElement.children.size)
            assertEquals("address", xmlElement.attributes[0].name)
            assertEquals("123 Main St, Springfield", xmlElement.attributes[0].value)
        }
    }

}