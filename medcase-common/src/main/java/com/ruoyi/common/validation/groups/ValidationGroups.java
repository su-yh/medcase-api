package com.ruoyi.common.validation.groups;

public final class ValidationGroups {
    public static final class Req {
        public interface Query {}
        public interface Create {}
        public interface Update {}
        public interface Delete {}
        public interface Others {}
    }

    public static final class Sql {
        public interface Insert {}
        public interface Update {}
        public interface Delete {}
    }

    public static final class Doctor {
        public interface Save {}
        public interface Submit {}
    }
}
