import React from "react";
import { CiBullhorn, CiChat1, CiSearch } from "react-icons/ci";
import { Avatar, AvatarFallback, AvatarImage } from "./ui/avatar";
import Link from "next/link";
import { useSession } from "next-auth/react";
import { getLastWord, toTitleCase } from "@/lib/utils";

const Navbar = () => {
  const { data: session, status } = useSession();

  return (
    <div className="flex items-center justify-between p-4">
      {/* SEARCH BAR */}
      <div className="hidden md:flex items-center gap-2 text-xs rounded-full ring-[1.5px] ring-gray-300 px-2">
        <CiSearch size={20} />
        <input
          type="text"
          placeholder="Search..."
          className="w-[200px] p-2 bg-transparent outline-none"
        />
      </div>
      {/* ICONS AND USER */}
      <div className="flex items-center justify-end gap-6 w-full">
        <div className="bg-white rounded-full w-9 h-9 flex items-center justify-center cursor-pointer">
          <CiChat1 size={20} />
        </div>

        <div className="bg-white rounded-full w-9 h-9 flex items-center justify-center cursor-pointer relative">
          <CiBullhorn size={20} />
          <div className="absolute -top-2 -right-2 w-5 h-5 flex items-center justify-center bg-purple-500 text-white rounded-full text-xs">
            1
          </div>
        </div>

        <Link
          href="/dashboard/profile"
          className="flex items-center justify-between gap-2"
        >
          <div className="flex flex-col justify-center">
            <span className="text-xs leading-3 font-medium">
              {toTitleCase((session?.user as any)?.user.username)}
            </span>
            {/* Edit this for more dynamic roles */}
            <span className="text-[10px] text-gray-500 text-right">
              {getLastWord(toTitleCase((session?.user as any)?.user.userType))}
            </span>
          </div>
          <Avatar>
            <AvatarImage src="https://github.com/shadcn.png" />
            <AvatarFallback>CN</AvatarFallback>
          </Avatar>
        </Link>
      </div>
    </div>
  );
};

export default Navbar;
