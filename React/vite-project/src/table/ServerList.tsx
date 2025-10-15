import React from "react";
import GenericTable from "../components/Table";
import type { Server } from "../type/Server";

const ServerTable: React.FC = () => {
//   const [servers, setServers] = useState<Server[]>([]);

//   useEffect(() => {
//     fetch("http://localhost:8080/api/servers")
//       .then((res) => res.json())
//       .then((data: Omit<Server, "id">[]) =>
//         data.map((s, i) => ({ ...s, id: i + 1 }))
//       )
//       .then(setServers)
//       .catch((err) => console.error("서버 데이터 불러오기 실패:", err));
//   }, []);

  const servers: Server[] = [
    {
      id: 1,
      name: "Lobby Server",
      type: "Server",
      path: "/lobby",
      host: "192.168.0.11",
      port: 25565,
      min: 0,
      max: 100,
    },
    {
      id: 2,
      name: "RPG Server",
      type: "Proxy",
      path: "/rpg",
      host: "192.168.0.12",
      port: 25566,
      min: 5,
      max: 50,
    },
  ];

  // 🧩 컬럼 정의
  const headers = [
    { key: "id" as const, label: "No" },
    { key: "name" as const, label: "이름" },
    { key: "type" as const, label: "타입" },
    { key: "path" as const, label: "경로" },
    { key: "host" as const, label: "호스트" },
    { key: "port" as const, label: "포트" },
    {
      key: "min" as const,
      label: "메모리",
      render: (row: Server) => `${row.min}GB ~ ${row.max}GB`,
    },
  ];

  return (
    <div className="container mt-4">
      <GenericTable<Server> columns={headers} data={servers} />
    </div>
  );
};

export default ServerTable;