"use client";

import AddPlayerForm from "@/components/forms/AddPlayerForm";
import TournamentUpdateForm from "@/components/forms/TournamentUpdateForm";
import Table from "@/components/Table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import axiosInstance from "@/lib/axios";
import { formatReadableDate, toTitleCase } from "@/lib/utils";
import clsx from "clsx";
import { useSession } from "next-auth/react";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";
import { set } from "react-hook-form";
import { CiCirclePlus } from "react-icons/ci";

const SingleTournamentPage = ({
  params,
}: {
  params: Promise<{ slug: string }>;
}) => {
  const { data: session, status } = useSession();
  const [tournament, setTournaments] = useState<Tournament>();
  const [matches, setMatches] = useState<Match[]>([]);
  const [players, setPlayers] = useState<Player[]>([]);
  const [tournamentId, setTournamentId] = useState<number>();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  // Fetch data using Axios
  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        const id = await params.id;
        const tournamentRes = await axiosInstance.get("/api/tournament/" + id);
        setTournaments(tournamentRes.data);
        setMatches(tournamentRes.data.matches);
        setPlayers(tournamentRes.data.players);
        setTournamentId(tournamentRes.data.id);
      } catch (err) {
        console.error("Error fetching tournaments:", err);
        setError("Failed to load tournaments.");
        setLoading(false);
      }
    };

    fetchTournaments();
  }, []);

  console.log(tournament);

  const renderRow = (item: Match) => (
    <tr
      key={item.id}
      className="border-b border-gray-200 even:bg-slate-50 text-sm hover:bg-lamaPurpleLight"
    >
      <td className="flex items-center gap-4 p-4">
        {/* <Image
          src="/"
          alt=""
          width={40}
          height={40}
          className="md:hidden xl:block w-10 h-10 rounded-full object-cover"
        /> */}
        <div className="flex flex-col">
          <h3 className="font-semibold">
            {item.player1.name} vs {item.player2.name}
          </h3>
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

  const renderPlayerRow = (item: PlayerLeaderBoard) => (
    <tr
      key={item.id}
      className="border-b border-gray-200 even:bg-slate-50 text-sm hover:bg-lamaPurpleLight"
    >
      <td className="flex items-center gap-4 p-4">
        <div className="flex flex-col">
          <h3 className="font-semibold">{item.name}</h3>
          <p className="text-xs text-gray-500">{item.username}</p>
        </div>
      </td>
      <td>
        <div className="flex items-center gap-2">
          <Link href={`/dashboard/list/users/${item.id}`}>
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
              <h1 className="text-xl font-semibold">{tournament?.name}</h1>
              {(session?.user as any)?.user.userType === "ROLE_ADMIN" && (
                <TournamentUpdateForm />
              )}
            </div>
            <p className="text-sm text-gray-500">
              The Ultimate Challenge Cup 2024 is an annual, high-stakes
              tournament that brings together competitors from across the globe
              to test their skills in an electrifying series of matches. From
              seasoned professionals to rising stars, this competition showcases
              talent, strategy, and sportsmanship at its finest. Competitors
              will face off in both single and team events, battling through
              intense rounds for the coveted title and ultimate bragging rights.
            </p>
            <div className="flex items-center justify-between gap-2 flex-wrap text-xs font-medium">
              <div className="w-full md:w-1/3 lg:w-full 2xl:w-1/3 flex items-center gap-2">
                <Image src="/date.png" alt="" width={14} height={14} />
                <span>{formatReadableDate(tournament?.startDate)}</span>
              </div>
              <div className="w-full md:w-1/3 lg:w-full 2xl:w-1/3 flex items-center gap-2">
                <Image src="/mail.png" alt="" width={14} height={14} />
                <span>admin@tournament.com</span>
              </div>
              <div className="w-full md:w-1/3 lg:w-full 2xl:w-1/3 flex items-center gap-2">
                <Image src="/phone.png" alt="" width={14} height={14} />
                <span>+65 1234 5678</span>
              </div>
            </div>
          </div>
        </div>
        {/* BOTTOM */}
        <Sheet>
          <div className="mt-4 bg-white rounded-md p-4 min-h-[800px]">
            <Tabs defaultValue="players" className="w-full">
              <div className="flex justify-between">
                <TabsList className="mb-1">
                  <TabsTrigger value="players">Players</TabsTrigger>
                  <TabsTrigger value="match">Matches</TabsTrigger>
                </TabsList>
                {(session?.user as any)?.user.userType === "ROLE_ADMIN" && (
                  <SheetTrigger>
                    <Button className="h-8 w-8 p-0 rounded-full bg-lamaSky hover:bg-lamaSky">
                      <CiCirclePlus size={30} />
                    </Button>
                  </SheetTrigger>
                )}
              </div>
              <TabsContent value="players">
                <Table
                  columns={playerColumns}
                  renderRow={renderPlayerRow}
                  data={players}
                />
              </TabsContent>
              <TabsContent value="match">
                <Table columns={columns} renderRow={renderRow} data={matches} />
              </TabsContent>
            </Tabs>
          </div>
          <SheetContent>
            <SheetHeader>
              <SheetTitle>Adding Players</SheetTitle>
              <SheetDescription>Pick you fighters!</SheetDescription>
            </SheetHeader>
            <AddPlayerForm
              currentPlayers={players}
              tournamentId={tournamentId}
            />
          </SheetContent>
        </Sheet>
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

const playerColumns = [
  { header: "Info", accessor: "info" },
  // {
  //   header: "Elo",
  //   accessor: "tournament",
  //   className: "hidden lg:table-cell",
  // },
  {
    header: "Actions",
    accessor: "action",
  },
];

export default SingleTournamentPage;
