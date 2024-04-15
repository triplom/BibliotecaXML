package test

import XMLDocument
import XMLElement
import XPathEvaluator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class XMLTests {

    // Test to add attribute
    @Test
    fun testAddAttribute() {
        val element = XMLElement("element")

        element.addAttribute("attribute", "value")
        assertEquals(1, element.attributes.size)
    }

    // Test to remove attribute
    @Test
    fun testRemoveAttribute(){
        val element = XMLElement("element")
        element.addAttribute("attribute", "value")
        element.removeAttribute("attribute")
        assertEquals(0, element.attributes.size)
    }


    // Teste para adicionar e remover uma entidade
    @Test
    fun testAddEntity() {
        val parent = XMLElement("parent")
        val child = XMLElement("child")

        parent.addChild(child)
        assertEquals(1, parent.children.size)
    }

    // Teste para adicionar e remover uma entidade
    @Test
    fun testRemoveEntity() {
        val parent = XMLElement("parent")
        val child = XMLElement("child")

        parent.addChild(child)

        parent.removeChild(child)
        assertEquals(0, parent.children.size)
    }

    @Test
    fun testFindAncestry(){
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")

        parent.addChild(child1)
        parent.addChild(child2)

        val child3 = XMLElement("child3")
        val child4 = XMLElement("child4")

        child1.addChild(child3)
        child3.addChild(child4)

        val testFindAncestryList = mutableListOf<XMLElement>(parent,child1, child3)
        assertEquals(testFindAncestryList.asReversed(), child4.findAncestry())

    }

    @Test
    fun testFindDescendants(){
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")

        parent.addChild(child1)
        parent.addChild(child2)

        val child3 = XMLElement("child3")
        val child4 = XMLElement("child4")

        child1.addChild(child3)
        child3.addChild(child4)

        val testFindDescendantsList = mutableListOf<XMLElement>(child1, child3, child4, child2)
        assertEquals(testFindDescendantsList, parent.findDescendants())
    }

/*
    // Teste para acessar o pai e os filhos de um elemento
    @Test
    fun testAccessParentAndChildren() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")

        parent.addChild(child1)
        parent.addChild(child2)

        assertEquals(parent, child1.parent)
        assertEquals(parent, child2.parent)
        assertEquals(2, parent.children.size)
    }
*/
    // Teste para verificar o método prettyPrint() de XMLDocument
    @Test
    fun testPrettyPrint() {
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root

        val expected = "<root>\n\t<child/>\n</root>"
        val actual = document.prettyPrint().trim() // Remover espaços em branco extras

        assertEquals(expected, actual)


        val child2 = XMLElement("child2")
        child.addChild(child2)
        val expectedwithDescendants = "<root>\n\t<child>\n\t\t<child2/>" +
                "\n\t</child>\n</root>"

        assertEquals(expectedwithDescendants, document.prettyPrint().trim())
    }

    @Test
    fun testHasAttribute(){
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root
        child.addAttribute("Teste", "Hello")
        assertEquals(true, child.hasAttribute("Teste"))

        assertEquals(false, child.hasAttribute("Mais um teste"))
    }

    @Test
    fun testAddAttributeGlobally(){
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root
        child.addAttribute("Teste", "Hello")
        val child2 = XMLElement("child")
        child.addChild(child2)

        document.addAttributesGlobal("child","Mais um teste","HelloWorld")
        assertEquals(true, child.hasAttribute("Mais um teste"))
        assertEquals(true, child2.hasAttribute("Mais um teste"))


        assertEquals(false, child2.hasAttribute("Alguém pediu um teste?"))

    }

    @Test
    fun testRenameAttributeGlobal(){
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root
        child.addAttribute("Test", "Hello")
        val child2 = XMLElement("child")
        child.addChild(child2)

        document.addAttributesGlobal("child","Mais um test","HelloWorld")

        assertEquals(true, child.hasAttribute("Mais um test"))

        document.renameAttributesGlobal("child","Mais um test", "Test2")
        assertEquals(true, child2.hasAttribute("Test2"))
        assertEquals(true, child.hasAttribute("Test2"))


        assertEquals(false, child.hasAttribute("Mais um test"))
    }

    @Test
    fun testRenameEntityGlobal(){
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
        assertEquals(true, child.name =="Batata")
        assertEquals(false, child.name =="child")
    }

    // Teste para verificar a avaliação de XPath em um documento XML
    @Test
    fun testXPathEvaluation() {
        val root = XMLElement("root")
        val child1 = XMLElement("child")
        val child2 = XMLElement("child")
        root.addChild(child1)
        root.addChild(child2)

        val document = XMLDocument()
        document.rootElement = root

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        assertEquals(2, result.size)
    }

    // Teste para verificar a avaliação de XPath em um documento XML com um único elemento filho
    @Test
    fun testXPathEvaluationSingleChild() {
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        val document = XMLDocument()
        document.rootElement = root

        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        assertEquals(1, result.size)
    }

    // Teste para verificar a avaliação de XPath em um documento XML com atributos
    @Test
    fun testXPathEvaluationWithAttributes() {
        val root = XMLElement("root")
        val child = XMLElement("child")
        child.addAttribute("attribute1", "value1")
        child.addAttribute("attribute2", "value2")
        root.addChild(child)

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
        val result = evaluator.evaluate("parent/child1/child2")

        assertEquals(1, result.size)
    }
}
