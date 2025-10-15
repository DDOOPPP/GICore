import type React from "react";

export type NavItem = {
    label: string;
    path: string;
    icon? : React.ReactNode;
    external?: boolean; //외부링크 여부
}