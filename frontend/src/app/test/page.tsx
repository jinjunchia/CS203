import React from "react";
import MatchBox from "./MatchBox";

const Test = () => {
  return (
    <div className="flex justify-center items-center gap-10">
      <div className="min-h-screen flex flex-col justify-center items-center gap-5">
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
      </div>
      <div className="min-h-screen flex flex-col justify-center items-center gap-5">
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
      </div>
      <div className="min-h-screen flex flex-col justify-center items-center gap-5">
        <MatchBox />
      </div>
    </div>
  );
};

export default Test;
