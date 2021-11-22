package exercisis


import com.db4o.Db4oEmbedded
import exercisis.GestionarRutesBD.*
fun main() {
    val bd = Db4oEmbedded.openFile ("Rutes.db4o")
    val gRutes = GestionarRutesBD()

    for (r in gRutes.llistat())
        bd.store(r)
    gRutes.close()
    bd.close()
}
