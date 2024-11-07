"use client";

import FormModal from "@/components/FormModel";
import Table from "@/components/Table";
import { Badge } from "@/components/ui/badge";
import axiosInstance from "@/lib/axios";
import { formatReadableDate, toTitleCase } from "@/lib/utils";
import clsx from "clsx";
import { useSession } from "next-auth/react";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";

const SingleTournamentPage = ({
  params,
}: {
  params: Promise<{ slug: string }>;
}) => {
  const { data: session, status } = useSession();
  const [match, setMatch] = useState<Match>();
  const [players, setPlayers] = useState<PlayerLeaderBoard[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch data using Axios
  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        const id = await params.id;
        const response = await axiosInstance.get("/api/player/" + id); // change to /api/user
        setMatch(response.data);
        setPlayers(response.data.matches);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching match:", err);
        setError("Failed to load match.");
        setLoading(false);
      }
    };

    fetchTournaments();
  }, []);

  console.log(players);

  const renderRow = (item: PlayerLeaderBoard) => (
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
      <td className="hidden md:table-cell">{item.eloRating}</td>
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
              {/* <h1 className="text-xl font-semibold">
                {match?.tournament.name}
              </h1> */}
              {(session?.user as any)?.user.userType === "ROLE_ADMIN" && (
                <FormModal
                  table="tournamentUpdate"
                  type="update"
                  data={match}
                />
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
                <span>{formatReadableDate(match?.matchDate)}</span>
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
        <div className="mt-4 bg-white rounded-md p-4 h-[800px]">
          <h1>Players</h1>
          <Table columns={columns} renderRow={renderRow} data={players} />
        </div>
      </div>
    </div>
  );
};

const columns = [
  { header: "Info", accessor: "info" },
  {
    header: "Elo",
    accessor: "tournament",
    className: "hidden lg:table-cell",
  },
  {
    header: "Actions",
    accessor: "action",
  },
];

export default SingleTournamentPage;
