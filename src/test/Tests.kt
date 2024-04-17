import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class XMLTests {

    private lateinit var document: XMLDocument
// Aumentar a arvore de testes

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
        assertTrue(document.rootElement?.children?.first()?.hasAttribute("AnotherTest") ?: false)
    }

    @Test
    fun `test rename attribute globally`() {
        // Teste para garantir que podemos renomear um atributo em todos os elementos filhos de um elemento específico.
        val child = document.rootElement?.children?.first()
        child?.addAttribute("OldName", "Value")
        document.renameAttributesGlobal("child", "OldName", "NewName")
        assertTrue(child?.hasAttribute("NewName") ?: false)
        assertFalse(child?.hasAttribute("OldName") ?: true)
    }

    @Test
    fun `test rename entity globally`() {
        // Teste para garantir que podemos renomear um elemento em todos os elementos filhos de um elemento específico.
        val child = document.rootElement?.children?.first()
        document.renameEntitiesGlobal("child", "newChild")
        assertEquals("newChild", child?.name)
    }

    @Test
    fun `test XPath evaluation`() {
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

        // Verificando se o resultado possui exatamente 5 elementos, pois esperamos encontrar os cinco filhos de "root"
        //assertEquals(5, result.size)
    }
}