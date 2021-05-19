package com.subscription.tracker.android.services.utils;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserClub;

public class RoleUtils {

    private RoleUtils() {}

    public static boolean isVisitor(final SingleUserData singleUserResponse) {
        for (UserClub userClub : singleUserResponse.getUserClubs()) {
            if (userClub.isActiveClub()) {
                return userClub.getRole().getRoleName().equals("VISITOR");
            }
        }
        return false;
    }

    public static int getRoleTextIdByName(final String roleName) {
        final Roles role = Roles.valueOf(roleName);
        return role.getRoleTextId();
    }

    public static int getRoleTextIdByRoleId(final int roleId) {
        for (Roles value : Roles.values()) {
            if (value.getRoleId() == roleId) {
                return value.getRoleTextId();
            }
        }
        return 0;
    }

    public static boolean isRoleAllowedToModify(final int requestorRole, final Roles value) {
        switch (requestorRole) {
            case 1: //allow all for admin
                return value.getRoleId() != 1;
            case 2: // owner can set another owner or add/change instructor
                return value.getRoleId() == 2 || value.getRoleId() == 3 || value.getRoleId() == 4;
            default: // instructor can not change roles
                return false;
        }
    }

    public enum Roles
    {
        ADMIN(1, R.string.user_role_text_admin),
        OWNER(2, R.string.user_role_text_owner),
        INSTRUCTOR(3, R.string.user_role_text_instructor),
        VISITOR(4, R.string.user_role_text_visitor);

        private final int roleId;
        private final int roleTextId;

        Roles(final int roleId, final int roleTextId) {
            this.roleId = roleId;
            this.roleTextId = roleTextId;
        }

        public int getRoleId() {
            return roleId;
        }

        public int getRoleTextId() {
            return roleTextId;
        }

    }

}
