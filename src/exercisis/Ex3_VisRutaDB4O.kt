package exercisis

import classesEmpleat.Empleat
import com.db4o.Db4oEmbedded
import exercisis.GestionarRutesBD.*
fun main() {
    val bd = Db4oEmbedded. openFile ("Rutes.db4o")
    var llistaDePunts= mutableListOf<PuntGeo>()
    val patro = Ruta(null,null,null,llistaDePunts)

    val llista = bd.queryByExample<Ruta>(patro)
    for (e in llista) {
        System.out.println(e.nom +": "+e.llistaDePunts.size+" punts")
    }

    bd.close()
}
