package gencell.croncargaarchivos.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-10-26T17:17:26")
@StaticMetamodel(Cron.class)
public class Cron_ { 

    public static volatile SingularAttribute<Cron, String> cron;
    public static volatile SingularAttribute<Cron, String> descripcion;
    public static volatile SingularAttribute<Cron, Date> fechaInicio;
    public static volatile SingularAttribute<Cron, Integer> idCron;
    public static volatile SingularAttribute<Cron, Date> fechaFinal;
    public static volatile SingularAttribute<Cron, String> url;

}