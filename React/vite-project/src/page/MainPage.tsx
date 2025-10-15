import React from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { FaServer, FaUsers, FaChartLine, FaCog } from "react-icons/fa";

const MainPage: React.FC = () => {
  return (
    <Container fluid className="p-4">
      <div className="mb-5 text-center">
        <h1 className="display-4 fw-bold text-primary mb-3">
          서버 관리 시스템
        </h1>
        <p className="lead text-muted">
          효율적인 서버 모니터링 및 관리 플랫폼
        </p>
      </div>

      <Row className="g-4 mb-4">
        <Col md={3}>
          <Card className="shadow-sm h-100 text-center border-0">
            <Card.Body className="d-flex flex-column justify-content-center">
              <FaServer className="mx-auto mb-3 text-primary" size={48} />
              <Card.Title className="fw-bold">서버 관리</Card.Title>
              <Card.Text className="text-muted">
                실시간 서버 상태 모니터링
              </Card.Text>
              <h3 className="text-primary fw-bold mt-2">2대</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm h-100 text-center border-0">
            <Card.Body className="d-flex flex-column justify-content-center">
              <FaUsers className="mx-auto mb-3 text-success" size={48} />
              <Card.Title className="fw-bold">접속자 수</Card.Title>
              <Card.Text className="text-muted">
                현재 접속 중인 사용자
              </Card.Text>
              <h3 className="text-success fw-bold mt-2">0명</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm h-100 text-center border-0">
            <Card.Body className="d-flex flex-column justify-content-center">
              <FaChartLine className="mx-auto mb-3 text-warning" size={48} />
              <Card.Title className="fw-bold">메모리 사용률</Card.Title>
              <Card.Text className="text-muted">
                전체 서버 평균 사용량
              </Card.Text>
              <h3 className="text-warning fw-bold mt-2">0%</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm h-100 text-center border-0">
            <Card.Body className="d-flex flex-column justify-content-center">
              <FaCog className="mx-auto mb-3 text-info" size={48} />
              <Card.Title className="fw-bold">시스템 상태</Card.Title>
              <Card.Text className="text-muted">
                전체 시스템 동작 상태
              </Card.Text>
              <h3 className="text-info fw-bold mt-2">정상</h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="g-4">
        <Col md={8}>
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-primary text-white fw-bold">
              최근 활동
            </Card.Header>
            <Card.Body>
              <ul className="list-unstyled mb-0">
                <li className="mb-2 pb-2 border-bottom">
                  <small className="text-muted">10분 전</small>
                  <div>Lobby Server 시작됨</div>
                </li>
                <li className="mb-2 pb-2 border-bottom">
                  <small className="text-muted">25분 전</small>
                  <div>RPG Server 시작됨</div>
                </li>
                <li className="mb-2">
                  <small className="text-muted">1시간 전</small>
                  <div>시스템 초기화 완료</div>
                </li>
              </ul>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4}>
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-success text-white fw-bold">
              빠른 작업
            </Card.Header>
            <Card.Body className="d-grid gap-2">
              <Button variant="outline-primary">서버 추가</Button>
              <Button variant="outline-secondary">설정 변경</Button>
              <Button variant="outline-info">로그 확인</Button>
              <Button variant="outline-warning">백업 실행</Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default MainPage;
