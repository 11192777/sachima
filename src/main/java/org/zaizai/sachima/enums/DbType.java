package org.zaizai.sachima.enums;

import org.zaizai.sachima.util.FnvHashUtils;

public enum DbType {
    other(1),
    oracle(1 << 1),
    mysql(1 << 2),
    oceanbase(1 << 3),
    ali_oracle(1 << 4),
    oceanbase_oracle(1 << 5),

    /**
     * MySQL和
     */
    mysql_liquibase(1 << 6),
    ;

    public final long mask;
    public final long hashCode64;

    DbType(long mask) {
        this.mask = mask;
        this.hashCode64 = FnvHashUtils.hashCode64(name());
    }

    public static long of(DbType... types) {
        long value = 0;

        for (DbType type : types) {
            value |= type.mask;
        }

        return value;
    }

    public static DbType of(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        try {
            return valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isOracleDbType(DbType dbType) {
        return DbType.oracle == dbType || DbType.oceanbase == dbType || DbType.ali_oracle == dbType;
    }

}
