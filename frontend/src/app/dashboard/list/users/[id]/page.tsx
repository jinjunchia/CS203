"use client";

import Table from "@/components/Table";
import EloLineGraph from "@/components/charts/EloLineGraphPlayer";
import PieChartGraph from "@/components/charts/PieChartPlayer";
import RadialGraph from "@/components/charts/RadialGraphsPlayer";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import axiosInstance from "@/lib/axios";
import { formatReadableDate, toTitleCase } from "@/lib/utils";
import clsx from "clsx";
import { Link } from "lucide-react";
import { useSession } from "next-auth/react";
import Image from "next/image";
import { useEffect, useState } from "react";

const SingleUserPage = ({ params }: { params: Promise<{ slug: string }> }) => {
  const { data: session, status } = useSession();
  const [user, setUser] = useState<User>();
  const [eloRecords, setEloRecords] = useState<any[]>([]);
  const [playerStats, setPlayerStats] = useState<any[]>([]);
  const [matches, setMatches] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch data using Axios
  useEffect(() => {
    const fetchUser = async () => {
      try {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        const id = await params.id;
        const userRes = await axiosInstance.get("/api/user/" + id, {
          withCredentials: true,
        });
        setUser(userRes.data);

        const eloRecordRes = await axiosInstance.get(
          "/api/elo-records/player/" + id,
          {
            withCredentials: true,
          }
        );
        setEloRecords(eloRecordRes.data);

        const playerStatsRes = await axiosInstance.get(
          "/api/player-stats/player/" + id,
          {
            withCredentials: true,
          }
        );
        setPlayerStats(playerStatsRes.data);

        const matchRes = await axiosInstance.get("/api/match/player/" + id, {
          withCredentials: true,
        });
        setMatches(matchRes.data);
        // console.log(matchRes.data);
      } catch (err) {
        console.error("Error fetching match:", err);
        setError("Failed to load match.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const renderRow = (item: Match) => (
    <tr
      key={item.id}
      className="border-b border-gray-200 even:bg-slate-50 text-sm hover:bg-lamaPurpleLight"
    >
      <td className="flex items-center gap-4 p-4">
        <div className="flex flex-col">
          <h3 className="font-semibold">
            {item.player1.name} vs {item.player2.name}
          </h3>
          <p className="text-xs text-gray-500">{item.tournament.name}</p>
        </div>
      </td>
      <td className="hidden md:table-cell">{item.id}</td>
      <td className="hidden md:table-cell">
        <Badge
          className={clsx({
            "bg-yellow-500": item.status === "SCHEDULED",
            "bg-green-500": item.status === "PENDING",
            "bg-blue-500": item.status === "COMPLETED",
          })}
        >
          {toTitleCase(item.status)}
        </Badge>
      </td>
      <td>
        {item.status === "COMPLETED" || item.status === "BYE"
          ? item.player1Score + " : " + item.player2Score
          : "Undecided"}
      </td>
      <td>{formatReadableDate(item.matchDate)}</td>
      <td>
        <div className="flex items-center gap-2">
          <Link href={`/dashboard/list/matches/${item.id}`}>
            <button className="w-7 h-7 flex items-center justify-center rounded-full bg-lamaSky">
              <Image src="/view.png" alt="" width={16} height={16} />
            </button>
          </Link>
        </div>
      </td>
    </tr>
  );

  return (
    <div className="flex-1 p-4 flex flex-col gap-4 xl:flex-row">
      {/* LEFT */}
      <div className="w-full">
        {/* USER INFO CARD */}
        <div className="bg-lamaSky py-6 px-4 rounded-md flex-1 flex gap-4">
          <div className="w-3/12 flex items-center justify-center align-middle">
            <Image
              src="https://images.pexels.com/photos/9302141/pexels-photo-9302141.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
              alt=""
              width={144}
              height={144}
              className="w-52 h-52 rounded-full object-cover"
            />
          </div>
          <div className="w-9/12 flex flex-col justify-between gap-4">
            <div className="flex items-center gap-4">
              <h1 className="text-xl font-semibold">{user?.username}</h1>
            </div>
            <p className="text-sm text-gray-500">
              Lorem ipsum, dolor sit amet consectetur adipisicing elit. Illum
              modi voluptatem earum. Rem, nesciunt odit debitis veritatis quis,
              accusamus voluptatum quaerat, cumque quam tempora itaque magnam in
              adipisci ipsa quo!
            </p>
            <div className="flex items-center justify-between gap-2 flex-wrap text-xs font-medium">
              <div className="w-full md:w-1/3 lg:w-full 2xl:w-1/3 flex items-center gap-2">
                <Image src="/mail.png" alt="" width={14} height={14} />
                <span>{user?.email}</span>
              </div>
              <div className="w-full md:w-1/3 lg:w-full 2xl:w-1/3 flex items-center gap-2">
                <Image src="/profile.png" alt="" width={14} height={14} />
                <span>
                  {user?.userType === "ROLE_ADMIN" ? "Admin" : "Player"}
                </span>
              </div>
            </div>
          </div>
        </div>
        {/* BOTTOM */}
        <div className="mt-4 bg-white rounded-md p-4 min-h-[800px]">
          <Tabs defaultValue="stats" className="w-full">
            <TabsList className="mb-4">
              <TabsTrigger value="stats">Player Statistics</TabsTrigger>
              <TabsTrigger value="history">Match History</TabsTrigger>
            </TabsList>
            <TabsContent value="stats" className="grid grid-cols-2 gap-4">
              <PieChartGraph
                description="The pie chart shows the percentage breakdown of wins, losses, and draws in a set of matches."
                title="Win-Lose-Draw"
                data={matches}
              />
              <EloLineGraph
                description="A Timeline of Elo Progression"
                title="Past Elo History"
                data={eloRecords}
              />
              <RadialGraph
                data={playerStats}
                title="Statistics"
                description="The chart shows the breakdown of Punches, KOs, and Dodges in their career."
              />
            </TabsContent>
            <TabsContent value="history">
              <Table columns={columns} renderRow={renderRow} data={matches} />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  );
};

const columns = [
  { header: "Info", accessor: "info" },
  {
    header: "Match ID",
    accessor: "id",
    className: "hidden md:table-cell",
  },
  {
    header: "Status",
    accessor: "status",
    className: "hidden md:table-cell",
  },
  {
    header: "Score",
    accessor: "score",
    className: "hidden md:table-cell",
  },
  {
    header: "Match Date",
    accessor: "tournament",
    className: "hidden lg:table-cell",
  },
  {
    header: "Actions",
    accessor: "action",
  },
];

export default SingleUserPage;
