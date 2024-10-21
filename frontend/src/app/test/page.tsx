import React from "react";
import MatchBox from "./MatchBox";

const Test = () => {
  return (
    <div className="h-full flex justify-center items-center gap-10">
      <div className="h-full flex flex-col justify-center items-center gap-5">
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          {/* <MatchBox /> */}
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          {/* <MatchBox /> */}
        </div>
      </div>

      <div className="h-full flex flex-col justify-center items-center gap-5">
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/2 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
      </div>

      <div className="h-full flex flex-col justify-center items-center gap-5">
        <div className="h-2/3 flex flex-col justify-center items-center gap-5">
          <MatchBox />
        </div>
        <div className="h-1/3 flex flex-col justify-center items-center gap-5">
          {/* <MatchBox /> */}
        </div>
      </div>
    </div>
  );
};

export default Test;
