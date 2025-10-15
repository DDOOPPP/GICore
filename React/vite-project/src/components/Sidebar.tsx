import React, { useState } from "react";
import { Nav } from "react-bootstrap";
import type { NavItem } from "../type/NavItem";
import { GrLinkNext, GrLinkPrevious } from "react-icons/gr";

type SidebarProps = {
  items: NavItem[];
  collapsed?: boolean;
  onItemClick?: (path: string) => void;
};

const Sidebar: React.FC<SidebarProps> = ({ items, onItemClick }) => {
  const [collapsed, setCollapsed] = useState(false);

  const handleClick = (e: React.MouseEvent, item: NavItem) => {
    if (!item.external && onItemClick) {
      e.preventDefault();
      onItemClick(item.path);
    }
  };

  return (
    <div
      className={`d-flex flex-column bg-dark text-white p-3 transition-all ${
        collapsed ? "collapsed" : ""
      }`}
      style={{
        width: collapsed ? "80px" : "220px",
        minHeight: "100vh",
        transition: "width 0.3s ease",
      }}
    >
      <button
        className="btn btn-outline-light mb-4"
        onClick={() => setCollapsed(!collapsed)}
      >
        {collapsed ? <GrLinkNext /> : <GrLinkPrevious />}
      </button>

      <Nav className="flex-column">
        {items.map((item) =>
          item.external ? (
            <a
              key={item.path}
              href={item.path}
              target="_blank"
              rel="noopener noreferrer"
              className="text-white text-decoration-none mb-4 fs-5"
            >
              {item.icon} {collapsed ? "" : item.label}
            </a>
          ) : (
            <a
              key={item.path}
              href={item.path}
              onClick={(e) => handleClick(e, item)}
              className="text-white text-decoration-none mb-4 fs-5"
              style={{ cursor: "pointer" }}
            >
              {item.icon} {collapsed ? "" : item.label}
            </a>
          )
        )}
      </Nav>
    </div>
  );
};

export default Sidebar;
