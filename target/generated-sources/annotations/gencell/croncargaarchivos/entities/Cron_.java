package gencell.croncargaarchivos.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.8.v20190620-rNA", date="2022-11-10T11:24:58")
@StaticMetamodel(Cron.class)
public class Cron_ { 

    public static volatile SingularAttribute<Cron, String> cron;
    public static volatile SingularAttribute<Cron, String> descripcion;
    public static volatile SingularAttribute<Cron, Date> fechaInicio;
    public static volatile SingularAttribute<Cron, Integer> idCron;
    public static volatile SingularAttribute<Cron, Date> fechaFinal;
    public static volatile SingularAttribute<Cron, String> url;

}