import Link from "next/link";
import React from "react";

const MatchBox = () => {
  return (
    <Link
      href="/"
      className="w-40 flex flex-col justify-center items-center bg-slate-300 p-4 text-xs rounded-xl"
    >
      <div>20 Feb 2023</div>
      <div className="flex gap-3 justify-center items-center mt-2 text-sm">
        <div className="flex flex-col items-center">
          <div className="w-6 h-6 flex justify-center items-center bg-lime-100 mb-1">
            1
          </div>
          <abbr>BCS</abbr>
        </div>
        <div className="font-bold">:</div>
        <div className="flex flex-col items-center">
          <div className="w-6 h-6 flex justify-center items-center bg-lime-100 mb-1">
            1
          </div>
          <abbr>ABC</abbr>
        </div>
      </div>
    </Link>
  );
};

export default MatchBox;
