package exercisis

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class GestionarRutesBD() {
    var con:Connection
    val url = "jdbc:sqlite:Rutes.sqlite"
    var st :Statement
    init {
        this.con= DriverManager.getConnection(url)
        this.st = con.createStatement()

    }
    fun close(){
        con.close()
    }
    fun inserir(r: Ruta) {
        //
        val x = st.executeQuery("SELECT MAX(num_r) FROM RUTES").getInt(1) + 1

        var palabras=("INSERT INTO RUTES VALUES ($x,'${r.nom}',${r.desnivell},${r.desnivellAcumulat})")
        st.executeUpdate(palabras)

        for (i in 0 until r.size()) {
            palabras=("INSERT INTO PUNTS VALUES ($x,${i + 1},'${r.getPuntNom(i)}',${r.getPuntLatitud(i)},${r.getPuntLongitud(i)})")
            st.executeUpdate(palabras)
        }
    }
    fun buscar(i: Int): Ruta{
            val punts = arrayListOf<PuntGeo>()
            val puntsVoid = arrayListOf<PuntGeo>()
            var num_r=0
            var nom=""
            var des=0
            var desa=0
            var rutilla=Ruta("",0,0,puntsVoid)
            val querryString="SELECT * FROM RUTES"
            var rs = st.executeQuery(querryString)
            while (rs.next()) {
                if (rs.getInt(1)==i) {
                    num_r = rs.getInt(1)
                    nom = rs.getString(2).toString()
                    des = rs.getInt(3)
                    desa = rs.getInt(4)
                }
            }
            rs.close()
            rs = st.executeQuery("select * from Punts where num_r is ${num_r}")
            while (rs.next()) {
                punts.add(PuntGeo((rs.getString(3)), Coordenades(rs.getDouble(4),rs.getDouble(5))))
            }
            rutilla.nom=nom
            rutilla.desnivell=des
            rutilla.desnivellAcumulat=desa
            rutilla.llistaDePunts=punts

            rs.close()
            return rutilla
    }
    fun llistat(): ArrayList<Ruta>{
        var lista= arrayListOf<Ruta>()
        val querryString="SELECT count(*) FROM RUTES"
        val rs = st.executeQuery(querryString)
        for (i in 1 .. rs.getInt(1)){
           lista.add(buscar(i))
        }
        return lista

    }
    fun esborrar(i: Int){
        val qr1= "DELETE FROM RUTES WHERE num_r=$i"
        val qr2= "DELETE FROM PUNTS WHERE num_r=$i"
        st.executeUpdate(qr1)
        st.executeUpdate(qr2)
    }
    fun guardar(r: Ruta){
        val querryString="SELECT count(*) FROM RUTES"
        var rutilla:Ruta
        var existe=false
        val rs = st.executeQuery(querryString)
        var x=0
        for (i in 1 .. rs.getInt(1)){
           rutilla=buscar(i)
            if (rutilla.nom.equals(r.nom)) {
                existe = true
                x=i
                break
            }
        }
        if (!existe){
            inserir(r)
        }
        if (existe){
            val querry1="UPDATE RUTES SET desn = ${r.desnivell}, desn_ac = ${r.desnivellAcumulat} WHERE num_r = ${x};"
            st.executeUpdate(querry1)


        }
        val querryPuntos="select count(*)\n" +
                "FROM PUNTS\n" +
                "where num_r=$x"
        val numPunts = st.executeQuery(querryPuntos)
        val resto=r.llistaDePunts.size-numPunts.getInt(1)
        if (r.llistaDePunts.size>numPunts.getInt(1)){
            for (i in 0 until r.size()) {
                val querry2="UPDATE PUNTS " +
                        "SET nom_p = \'${r.getPuntNom(i)}\', latitud = ${r.getPuntLatitud(i)}, longitud = ${r.getPuntLongitud(i)} "+
                        "WHERE num_r = ${x} AND num_p = ${i+1};"
                st.executeUpdate(querry2)

            }
            val resto = st.executeQuery(querryPuntos)
            for(i in resto.getInt(1) until r.llistaDePunts.size){
                val palabras = ("INSERT INTO PUNTS VALUES ($x,${i + 1},'${r.getPuntNom(i)}',${r.getPuntLatitud(i)},${r.getPuntLongitud(i)})")
                st.executeUpdate(palabras)
                }

        }
    }
}
