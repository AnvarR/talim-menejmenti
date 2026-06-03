package com.edu.talim.entity.enums;

public enum StudentType {
        KURSANT,
        TINGLOVCHI;

        public static StudentType fromLabel(String label) {
                if (label == null || label.isEmpty()) return null;
                for (StudentType t : values()) {
                        if (t.name().equalsIgnoreCase(label.trim())) {
                                return t;
                        }
                }
                throw new RuntimeException("Noto'g'ri type: " + label);
        }
}