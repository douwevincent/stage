package cm.univ.maroua.enspm.stage.domain;

public enum NotificationReferenceDateType {
    DEBUT_STAGE,
    FIN_STAGE,

    @Deprecated
    DEBUT_PERIODE,

    @Deprecated
    FIN_PERIODE,

    JOURS_AVANT_FIN_STAGE,
    JOURS_APRES_FIN_STAGE
}
