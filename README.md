# XML Processing Project

Este projeto é um exemplo simples de processamento de XML em Kotlin. Ele inclui classes para representar elementos XML, documentos XML, avaliação de expressões XPath e testes unitários para garantir o correto funcionamento das funcionalidades implementadas.

## Funcionalidades Implementadas

- **XMLAttribute**: Classe que representa um atributo XML com um nome e um valor.
- **XMLElement**: Classe que representa um elemento XML, com métodos para adicionar/remover atributos e elementos filhos, além de permitir a visita de um XMLVisitor.
- **XMLDocument**: Classe que representa um documento XML, com método para imprimir o documento de forma bonita (prettyPrint).
- **XMLVisitor**: Interface para visitantes XML, com método para visitar um elemento XML.
- **XPathEvaluator**: Classe que avalia expressões XPath em um documento XML, retornando uma lista de elementos correspondentes.

## Testes Unitários

Os testes unitários garantem que as funcionalidades do projeto estão implementadas corretamente. Eles incluem testes para adicionar/remover entidades, adicionar/remover atributos, acessar pai/filhos de elementos, imprimir documentos de forma bonita e avaliar expressões XPath.

## Como Usar

Para usar este projeto, basta clonar o repositório e importá-lo em seu ambiente de desenvolvimento Kotlin. Você pode executar os testes unitários para garantir que todas as funcionalidades estão funcionando conforme esperado.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues relatando problemas ou sugestões de melhorias. Se preferir, faça um fork do repositório, implemente as mudanças e envie um pull request.

## Autor

Este projeto foi desenvolvido por @triplom e está sob a licença ...
