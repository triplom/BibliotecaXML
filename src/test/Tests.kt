import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import source.XMLElement
import source.XPathEvaluator
import kotlin.reflect.full.declaredMemberProperties

// Testes para verificar a funcionalidade das anotações XML
class XMLAnnotationsTest {

    // Teste para verificar se as propriedades foram convertidas corretamente com as anotações
    //ADICIONAR TESTE DE CONVERSÃO UTILIZANDO ANOTAÇÕES
}

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
        child1.addChild(child2)
        child2.addChild(child3)
        child3.addChild(child4)

        val expectedAncestryNames = listOf("root", "child1", "child2", "child3")
        val actualAncestryNames = child4.findAncestry().map { it.name }

        assertIterableEquals(expectedAncestryNames, actualAncestryNames)
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
    fun `test rename attribute globally`() {
        val child = document.rootElement?.children?.first()
        child?.addAttribute("OldName", "Value")
        document.renameAttributesGlobal("child", "OldName", "NewName")
        assertEquals("Value", child?.attributes?.find { it.name == "NewName" }?.value)
        assertNull(child?.attributes?.find { it.name == "OldName" })
    }

    @Test
    fun `test rename entity globally`() {
        val child = document.rootElement?.children?.first()
        document.renameEntitiesGlobal("child", "newChild")
        assertEquals("newChild", child?.name)
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
}

// Testes para a funcionalidade de XPath e avaliação no documento XML
class XMLTests {

    // Teste para avaliar a expressão XPath no documento XML
    @Test
    fun testXPathEvaluation() {
        // Criando um documento com um elemento raiz "root" e cinco elementos filhos "child1" a "child5"
        val document = source.XMLDocument()
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
//adicionar testes aqui