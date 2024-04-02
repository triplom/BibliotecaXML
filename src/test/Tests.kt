package test

import XMLDocument
import XMLElement
import XPathEvaluator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class XMLTests {
    // Teste para adicionar e remover uma entidade
    @Test
    fun testAddRemoveEntity() {
        // Criando um elemento pai e um elemento filho
        val parent = XMLElement("parent")
        val child = XMLElement("child")

        // Adicionando o elemento filho ao elemento pai
        parent.addChild(child)
        assertEquals(1, parent.children.size) // Verificando se o elemento filho foi adicionado corretamente

        // Removendo o elemento filho do elemento pai
        parent.removeChild(child)
        assertEquals(0, parent.children.size) // Verificando se o elemento filho foi removido corretamente
    }

    // Teste para adicionar e remover um atributo
    @Test
    fun testAddRemoveAttribute() {
        // Criando um elemento
        val element = XMLElement("element")

        // Adicionando um atributo ao elemento
        element.addAttribute("attribute", "value")
        assertEquals(1, element.attributes.size) // Verificando se o atributo foi adicionado corretamente

        // Removendo o atributo do elemento
        element.removeAttribute("attribute")
        assertEquals(0, element.attributes.size) // Verificando se o atributo foi removido corretamente
    }

    // Teste para acessar o pai e os filhos de um elemento
    @Test
    fun testAccessParentAndChildren() {
        // Criando um elemento pai e dois elementos filhos
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")

        // Adicionando os elementos filhos ao elemento pai
        parent.addChild(child1)
        parent.addChild(child2)

        // Verificando se os elementos filhos têm o pai correto e se o elemento pai tem os filhos corretos
        assertEquals(parent, child1.parent)
        assertEquals(parent, child2.parent)
        assertEquals(2, parent.children.size)
    }

    // Teste para verificar o método prettyPrint() de XMLDocument
    @Test
    fun testPrettyPrint() {
        // Criando uma estrutura XML simples
        val root = XMLElement("root")
        val child = XMLElement("child")
        root.addChild(child)

        // Criando um documento XML e atribuindo a raiz
        val document = XMLDocument()
        document.rootElement = root

        // String esperada após prettyPrint
        val expected = "<root>\n\t<child/>\n</root>"

        // Verificando se o resultado do prettyPrint é o esperado
        assertEquals(expected, document.prettyPrint())
    }

    // Teste para verificar a avaliação de XPath em um documento XML
    @Test
    fun testXPathEvaluation() {
        // Criando uma estrutura XML com dois elementos filhos com o mesmo nome
        val root = XMLElement("root")
        val child1 = XMLElement("child")
        val child2 = XMLElement("child")
        root.addChild(child1)
        root.addChild(child2)

        // Criando um documento XML e atribuindo a raiz
        val document = XMLDocument()
        document.rootElement = root

        // Criando um avaliador XPath e avaliando a expressão XPath
        val evaluator = XPathEvaluator(document)
        val result = evaluator.evaluate("root/child")

        // Verificando se a quantidade de elementos encontrados é a esperada
        assertEquals(2, result.size)
    }
}