@Target(AnnotationTarget.CLASS)
annotation class DBName(val newName:String)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ByDefault(val valueDefault:Double)


@DBname("fuc")
class FUC (
    val codigo:String,
    val nome:String,

    @ByDefault(6.0) val ects:Double,

    val observacoes : String,
    val avaliacao: List<ComponenteAvaliacao>
)

@DBname("componente")
class ComponenteAvaliacao(val nome: String, val peso: Int)