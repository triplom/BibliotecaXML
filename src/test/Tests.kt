import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMemberProperties

class XMLAnnotationsTest {

    data class Person(
        @XMLProperty(name = "ID")
        val id: Int,
        @XMLProperty(name = "Nome")
        val name: String,
        @XMLProperty(name = "Idade", type = XMLType.ATTRIBUTE)
        val age: Int
    )

    @Test
    fun testXMLAnnotations() {
        val person = Person(1, "John", 30)
        val xmlElement = objectToXML(person)

        // Verificando se as propriedades foram convertidas corretamente com as anotações
        assertEquals(3, xmlElement.attributes.size)

        // Verificando os nomes e valores dos atributos
        assertEquals("1", xmlElement.attributes.find { it.name == "ID" }?.value)
        assertEquals("John", xmlElement.attributes.find { it.name == "Nome" }?.value)
        assertEquals("30", xmlElement.attributes.find { it.name == "Idade" }?.value)

        // Verificando se a propriedade "age" foi convertida como atributo
        assertEquals(0, xmlElement.children.size)
    }
}

class XMLDocumentTest {

    private lateinit var document: XMLDocument

    @BeforeEach
    fun setUp() {
        // Configuração inicial para cada teste: criamos um documento com um elemento raiz "root" e um elemento filho "child".
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)
        document = XMLDocument()
        document.rootElement = root
    }

    @Test
    fun `test add attribute`() {
        // Teste para garantir que podemos adicionar um atributo a um elemento.
        val element = XMLElement("element")
        element.addAttribute("attribute", "value")
        assertEquals(1, element.attributes.size)
    }

    @Test
    fun `test remove attribute`() {
        // Teste para garantir que podemos remover um atributo de um elemento.
        val element = XMLElement("element")
        element.addAttribute("attribute", "value")
        element.removeAttribute("attribute")
        assertEquals(0, element.attributes.size)
    }

    @Test
    fun `test add entity`() {
        // Teste para garantir que podemos adicionar um elemento filho a um elemento pai.
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addChild(child)
        assertEquals(1, parent.children.size)
    }

    @Test
    fun `test remove entity`() {
        // Teste para garantir que podemos remover um elemento filho de um elemento pai.
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addChild(child)
        parent.removeChild(child)
        assertEquals(0, parent.children.size)
    }

    @Test
    fun `test find ancestry`() {
        val root = XMLElement("root")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
        val child3 = XMLElement("child3")
        val child4 = XMLElement("child4")

        root.addChild(child1)
        child1.addChild(child2)
        child2.addChild(child3)
        child3.addChild(child4)

        val expectedAncestryNames = listOf("root", "child1", "child2", "child3")
        val actualAncestryNames = child4.findAncestry().map { it.name }

        assertIterableEquals(expectedAncestryNames, actualAncestryNames)
    }

    @Test
    fun `test find descendants`() {
        // Teste para garantir que podemos encontrar os descendentes de um elemento.
        val child4 = XMLElement("child4")
        val child3 = XMLElement("child3")
        val child2 = XMLElement("child2")
        val child1 = XMLElement("child1")
        val parent = XMLElement("parent")

        parent.addChild(child1)
        child1.addChild(child2)
        child2.addChild(child3)
        child3.addChild(child4)

        val descendantsList = mutableListOf<XMLElement>(child1, child2, child3, child4)
        assertEquals(descendantsList, parent.findDescendants())
    }

    @Test
    fun `test has attribute`() {
        // Teste para garantir que podemos verificar se um elemento possui um determinado atributo.
        val child = XMLElement("child")
        child.addAttribute("Test", "Hello")
        assertTrue(child.hasAttribute("Test"))
        assertFalse(child.hasAttribute("Another"))
    }

    @Test
    fun `test add attribute globally`() {
        // Teste para garantir que podemos adicionar um atributo a todos os elementos filhos de um elemento específico.
        document.addAttributesGlobal("child", "AnotherTest", "World")
        assertEquals("World", document.rootElement?.children?.first()?.attributes?.find { it.name == "AnotherTest" }?.value)
    }

    @Test
    fun `test rename attribute globally`() {
        // Teste para garantir que podemos renomear um atributo em todos os elementos filhos de um elemento específico.
        val child = document.rootElement?.children?.first()
        child?.addAttribute("OldName", "Value")
        document.renameAttributesGlobal("child", "OldName", "NewName")
        assertEquals("Value", child?.attributes?.find { it.name == "NewName" }?.value)
        assertNull(child?.attributes?.find { it.name == "OldName" })
    }

    @Test
    fun `test rename entity globally`() {
        // Teste para garantir que podemos renomear um elemento em todos os elementos filhos de um elemento específico.
        val child = document.rootElement?.children?.first()
        document.renameEntitiesGlobal("child", "newChild")
        assertEquals("newChild", child?.name)
    }
}

class XMLElementTest {

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
}

class XMLTests {

    @Test
    fun testXPathEvaluation() {
        // Criando um documento com um elemento raiz "root" e cinco elementos filhos "child1" a "child5"
        val document = XMLDocument()
        val root = XMLElement("root")
        val child = XMLElement("child")
        document.rootElement = root
        root.addChild(child)
        for (i in 1..5) {
            val secondLayerChild = XMLElement("child$i")
            child.addChild(secondLayerChild)
        }

        // Avaliando a expressão XPath "root/child" no documento
        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        assertEquals("<child1/>\n<child2/>\n<child3/>\n<child4/>\n<child5/>\n", result)
    }
}

class XMLReflectionTest {

    @Test
    fun testXMLReflection() {
        data class TestClass(val field1: String, val field2: Int, val field3: Double)

        val properties = TestClass::class.declaredMemberProperties
        assertEquals(3, properties.size)
        assertEquals("field1", properties.elementAt(0).name)
        assertEquals("field2", properties.elementAt(1).name)
        assertEquals("field3", properties.elementAt(2).name)
    }
}