# Biblioteca de Processamento de XML em Kotlin

Este projeto fornece uma solução robusta e flexível para processamento de XML em Kotlin. Ele oferece classes e utilitários para representar elementos XML, documentos XML, avaliação de expressões XPath e muito mais.

## Funcionalidades Implementadas

1. **Anotações e Adaptadores XML**:
   - `XMLString`, `XMLStringAdapter`, `AttributeInXML`: Anotações utilizadas para personalizar a representação XML de uma classe e suas propriedades.

2. **Classes Principais**:
   - `XMLType`, `XMLAttribute`, `XMLDocument`, `XMLElement`: Classes para representar e manipular documentos XML, elementos e atributos.

3. **Interfaces e Adaptadores**:
   - `XMLVisitor`, `XmlAdapterBase`, `XmlStringAdapter`: Interfaces e classes base para visitantes XML e adaptadores XML personalizados.

4. **Funções de Extensão**:
   - `Any.toXML()`: Função de extensão para converter qualquer objeto em um elemento XML.

5. **Avaliação de XPath**:
   - `XPathEvaluator`: Classe para avaliar expressões XPath em documentos XML.

## Testes Unitários

Os testes unitários garantem a funcionalidade correta das implementações. Eles abrangem cenários como adição/remoção de elementos e atributos, manipulação de documentos XML e avaliação de expressões XPath.

## Como Usar

Para utilizar esta biblioteca, basta clonar o repositório e importá-lo em seu ambiente de desenvolvimento Kotlin. Os testes unitários podem ser executados para garantir que todas as funcionalidades estejam operando conforme o esperado.

## Exemplo de Uso:
```kotlin
@XMLString(adapter = FUCAdapter::class)
data class Person(val name: String, val age: Int)

val person = Person("Alice", 30)
val xmlElement = person.toXML()

val nameAttribute = xmlElement.getAttribute("name")
val ageAttribute = xmlElement.getAttribute("age")
```
Neste exemplo, a anotação @XMLString é usada para indicar que a classe Person deve ser representada como uma string XML usando o adaptador FUCAdapter. A função de extensão toXML() é usada para converter o objeto Person em um XMLElement. As funções getAttribute() são usadas para obter os atributos XML do elemento raiz.

## Contribuição

Contribuições são bem-vindas! Você pode relatar problemas, sugerir melhorias ou contribuir diretamente implementando novos recursos. Sinta-se à vontade para abrir issues ou enviar pull requests.

## Autores

Este projeto foi desenvolvido por @triplom e @miguel-as-figueiredo 

## Explicações para cada etapa do código

1. **Anotações e Adaptadores XML**:
   - `XMLString`, `XMLStringAdapter`, `AttributeInXML`: Anotações e adaptadores para personalizar a representação XML de classes e propriedades.

2. **Classes Principais**:
   - `XMLType`: Enumeração para tipos de representação XML.
   - `XMLAttribute`: Representação de atributos XML.
   - `XMLDocument`: Representação de documentos XML.
   - `XMLElement`: Representação de elementos XML.

3. **Interfaces e Adaptadores**:
   - `XMLVisitor`: Interface para visitantes XML.
   - `XmlAdapterBase`: Interface base para adaptadores XML.
   - `XmlStringAdapter`: Interface para adaptadores de string XML.

4. **Funções de Extensão**:
   - `Any.toXML()`: Função para converter objetos em elementos XML.

5. **Avaliação de XPath**:
   - `XPathEvaluator`: Classe para avaliar expressões XPath em documentos XML.

Estas explicações fornecem uma visão detalhada de como cada parte do código se relaciona com as funcionalidades implementadas. Se precisar de mais informações sobre alguma parte específica, não hesite em perguntar!
