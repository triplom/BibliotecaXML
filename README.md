# Biblioteca de Processamento de XML em Kotlin

Este projeto é um exemplo simples de processamento de XML em Kotlin. Ele inclui classes para representar elementos XML,
documentos XML, avaliação de expressões XPath e testes unitários para garantir o correto funcionamento das
funcionalidades implementadas.

## Funcionalidades Implementadas

- **XMLAttribute**: Classe que representa um atributo XML com um nome e um valor.
- **XMLElement**: Classe que representa um elemento XML, com métodos para adicionar/remover atributos e elementos
  filhos, além de permitir a visita de um XMLVisitor.
- **XMLDocument**: Classe que representa um documento XML, com método para imprimir o documento de forma bonita (
  prettyPrint).
- **XMLVisitor**: Interface para visitantes XML, com método para visitar um elemento XML.
- **XPathEvaluator**: Classe que avalia expressões XPath em um documento XML, retornando uma lista de elementos
  correspondentes.

## Testes Unitários

Os testes unitários garantem que as funcionalidades do projeto estão implementadas corretamente. Eles incluem testes
para adicionar/remover entidades, adicionar/remover atributos, acessar pai/filhos de elementos, imprimir documentos de
forma bonita e avaliar expressões XPath.

## Como Usar

Para usar este projeto, basta clonar o repositório e importá-lo em seu ambiente de desenvolvimento Kotlin. Você pode
executar os testes unitários para garantir que todas as funcionalidades estão funcionando conforme esperado.

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

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues relatando problemas ou sugestões de melhorias. Se
preferir, faça um fork do repositório, implemente as mudanças e envie um pull request.

val nameAttribute = xmlElement.getAttribute("name")
val ageAttribute = xmlElement.getAttribute("age")
```
Neste exemplo, a anotação @XMLString é usada para indicar que a classe Person deve ser representada como uma string XML usando o adaptador FUCAdapter. A função de extensão toXML() é usada para converter o objeto Person em um XMLElement. As funções getAttribute() são usadas para obter os atributos XML do elemento raiz.

## Contribuição


Contribuições são bem-vindas! Você pode relatar problemas, sugerir melhorias ou contribuir diretamente implementando novos recursos. Sinta-se à vontade para abrir issues ou enviar pull requests.

## Autores

Este projeto foi desenvolvido por @triplom e @miguel-as-figueiredo 


1. **`XMLElement` e `XMLDocument`**: Definem as classes que representam elementos XML e documentos XML, respectivamente,
   junto com suas funcionalidades.

## Explicações para cada etapa do código


1. **Anotações e Adaptadores XML**:
   - `XMLString`, `XMLStringAdapter`, `AttributeInXML`: Anotações e adaptadores para personalizar a representação XML de classes e propriedades.


3. **Funções em `XMLElement`**:
    - `addAttribute`: Adiciona um novo atributo ao elemento.
    - `removeAttribute`: Remove um atributo existente do elemento.
    - `addChild`: Adiciona um novo elemento filho ao elemento atual.
    - `removeChild`: Remove um elemento filho existente do elemento atual.
    - `rename`: Renomeia o elemento.
    - `accept`: Aceita um visitante XML para permitir visitação do elemento e seus filhos.
    - `prettyPrint`: Gera uma representação em formato legível do elemento e seus filhos.

4. **Funções em `XMLDocument`**:
    - `prettyPrint`: Gera uma representação em formato legível do documento XML.
    - `writeToFile`: Escreve o documento XML em um arquivo (implementação será adicionada posteriormente).
    - `accept`: Aceita um visitante XML para permitir visitação do documento.

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

7. **`main`**: Função principal que demonstra o uso das classes e funções definidas acima:
    - Cria um documento XML com um elemento raiz e vários elementos filhos.
    - Renomeia o elemento raiz.
    - Escreve o documento em um arquivo (a implementação real será adicionada posteriormente).
    - Adiciona atributos aos elementos.
    - Visita os elementos do documento usando um visitante XML.
    - Imprime o documento XML de forma bonita.

Estas explicações fornecem uma visão detalhada de como cada parte do código se relaciona com as funcionalidades implementadas. Se precisar de mais informações sobre alguma parte específica, não hesite em perguntar!

