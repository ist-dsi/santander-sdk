package org.fenixedu.santandersdk.dto;

public enum GetRegisterStatus {
    READY_FOR_PRODUCTION("Preparado para Produção"),
    PRODUCTION("Em Produção"),
    REMI_REQUEST("Pedido de Reemissão"),
    RENU_REQUEST("Pedido de Renovação"),
    REJECTED_REQUEST("Emissão Rejeitada"),
    ISSUED("Expedido"),
    NO_RESULT("NoResult"),
    UNKNOWN("Unknown Status");

    private final String name;

    GetRegisterStatus(String name) {
        this.name = name;
    }

    public static GetRegisterStatus fromString(String text) {
        for (GetRegisterStatus b : GetRegisterStatus.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
