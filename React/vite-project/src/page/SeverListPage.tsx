// src/pages/ServerListPage.tsx
import React from "react";
import ServerList from "../table/ServerList";
import { Container, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const ServerListPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container className="mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-primary">서버 관리</h2>
        <Button
          variant="success"
          onClick={() => navigate("/servers/new")}
        >
          + 새 서버 추가
        </Button>
      </div>

      <ServerList />

      <p className="text-muted mt-4">
        총 2개의 서버가 등록되어 있습니다.
      </p>
    </Container>
  );
};

export default ServerListPage;
