"use client";

import Pagination from "@/components/Pagination";
import Table from "@/components/Table";
import TableSearch from "@/components/TableSearch";
import { Badge } from "@/components/ui/badge";
import axiosInstance from "@/lib/axios";
import { toTitleCase } from "@/lib/utils";
import clsx from "clsx";
import { useSession } from "next-auth/react";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";

const columns = [
  { header: "Info", accessor: "info" },
  {
    header: "Tournament ID",
    accessor: "id",
    className: "hidden md:table-cell",
  },
  {
    header: "Status",
    accessor: "status",
    className: "hidden md:table-cell",
  },
  {
    header: "Format",
    accessor: "format",
    className: "hidden md:table-cell",
  },
  {
    header: "Min Elo",
    accessor: "minEloRating",
    className: "hidden lg:table-cell",
  },
  {
    header: "Max Elo",
    accessor: "maxEloRating",
    className: "hidden lg:table-cell",
  },
  {
    header: "Actions",
    accessor: "action",
  },
];

const TournamentPage = () => {
  const { data: session, status } = useSession();
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  // Fetch data using Axios
  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        const response = await axiosInstance.get("/api/tournament");
        setTournaments(response.data);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching tournaments:", err);
        setError("Failed to load tournaments.");
        setLoading(false);
      }
    };

    fetchTournaments();
  }, []);

  const renderRow = (item: Tournament) => (
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
          <h3 className="font-semibold">{item.name}</h3>
          <p className="text-xs text-gray-500">{item.location}</p>
        </div>
      </td>
      <td className="hidden md:table-cell">{item.id}</td>
      <td className="hidden md:table-cell">
        <Badge
          className={clsx({
            "bg-yellow-500": item.status === "SCHEDULED",
            "bg-green-500": item.status === "ONGOING",
            "bg-blue-500": item.status === "COMPLETED",
          })}
        >
          {toTitleCase(item.status)}
        </Badge>
      </td>
      <td className="hidden md:table-cell">{toTitleCase(item.format)}</td>
      <td className="hidden lg:table-cell">{item.minEloRating}</td>
      <td className="hidden lg:table-cell">{item.maxEloRating}</td>
      <td>
        <div className="flex items-center gap-2">
          <Link href={`/list/tournaments/${item.id}`}>
            <button className="w-7 h-7 flex items-center justify-center rounded-full bg-lamaSky">
              <Image src="/view.png" alt="" width={16} height={16} />
            </button>
          </Link>
          {(session?.user as any)?.user.id === item.adminId && (
            <button className="w-7 h-7 flex items-center justify-center rounded-full bg-lamaPurple">
              <Image src="/delete.png" alt="" width={16} height={16} />
            </button>
          )}
        </div>
      </td>
    </tr>
  );

  return (
    <div className="bg-white p-4 rounded-ml flex-1 m-4 mt-0">
      {/* TOP */}
      <div className="flex items-center justify-between">
        <h1 className="hidden md:block text-lg font-semibold">
          All Tournament
        </h1>
        <div className="flex flex-col md:flex-row items-center gap-4 w-full md:w-auto">
          <TableSearch />
          <div className="flex items-center gap-4 self-end">
            <button className="w-8 h-8 flex items-center justify-center rounded-full bg-lamaYellow">
              <Image src="/filter.png" alt="" width={14} height={14} />
            </button>
            <button className="w-8 h-8 flex items-center justify-center rounded-full bg-lamaYellow">
              <Image src="/sort.png" alt="" width={14} height={14} />
            </button>
            <button className="w-8 h-8 flex items-center justify-center rounded-full bg-lamaYellow">
              <Image src="/plus.png" alt="" width={14} height={14} />
            </button>
          </div>
        </div>
      </div>
      {/* LIST */}
      <Table columns={columns} renderRow={renderRow} data={tournaments} />
      {/* PAGINATION */}
      <Pagination />
    </div>
  );
};

export default TournamentPage;
