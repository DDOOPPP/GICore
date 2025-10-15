import React from "react";
import { Container, Card, Row, Col } from "react-bootstrap";

const Dashboard: React.FC = () => {
  return (
    <Container fluid className="p-4">
      <h2 className="mb-4">대시보드</h2>

      <Row className="g-4">
        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <Card.Title>사용자 수</Card.Title>
              <Card.Text>총 1,245명의 사용자가 있습니다.</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <Card.Title>매출</Card.Title>
              <Card.Text>이번 달 매출: ₩3,420,000</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <Card.Title>알림</Card.Title>
              <Card.Text>새로운 알림이 5개 있습니다.</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
